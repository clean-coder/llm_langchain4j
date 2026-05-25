package parallel;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.service.AiServices;
import structuredData.Libraries;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.IO.println;
import static parallel.helper.FileHelper.writeToFile;
import static parallel.helper.ModelHelper.*;

///
/// Virtual threads (Project Loom, finalized in Java 21) are ideal for I/O-bound tasks like LLM calls.
/// Unlike CompletableFuture.supplyAsync(), which borrows threads from the common ForkJoinPool,
/// Executors.newVirtualThreadPerTaskExecutor() creates one lightweight virtual thread per submitted task.
/// Virtual threads block cheaply — the JVM unmounts them from the carrier thread while waiting for I/O,
/// so thousands of concurrent LLM calls consume far fewer OS resources than platform threads would.
///
/// Here's what differs from ParallelChainsWithAiServices:
///
/// | Aspect | CompletableFuture version   | Virtual threads version |
/// |---|---|---|
/// | Thread source  | Common ForkJoinPool (platform threads) |One virtual thread per task  |
/// | Blocking cost | Ties up a pool thread during I/O wait |JVM unmounts from carrier thread — near zero cost  |
/// | Backpressure | Pool can exhaust under load |Scales to thousands of concurrent calls  |
/// | API | CompletableFuture<T> |Future<T> via ExecutorService.submit()       |
/// | Lifecycle | Manual join  |try-with-resources closes executor after the block |
///
///   The logic (fan-out → collect → consolidate) is identical. The key change is Executors.newVirtualThreadPerTaskExecutor() inside a try-with-resources, which
///   creates one lightweight virtual thread per submitted task and automatically shuts down cleanly when all futures are resolved.
///
public class ParallelChainsWithVirtualThreads {

    @StructuredPrompt("""
            What are the {{numOfItems}} most popular programming libraries in {{language}} for accessing LLMs?
            Reply with name, provider, url, language, version for each library""")
    record LibraryPrompt(int numOfItems, String language) {
    }

    interface LibraryService {
        @dev.langchain4j.service.SystemMessage("You are an ai developer expert.")
        Libraries getLibraries(@dev.langchain4j.service.UserMessage LibraryPrompt prompt);
    }

    private static Libraries getLLMLibraries(ChatModel model, String language, int noOfItems) {
        LibraryService service = AiServices.builder(LibraryService.class)
                .chatModel(model)
                .build();

        var prompt = new LibraryPrompt(noOfItems, language);
        Libraries libraries = service.getLibraries(prompt);

        println("Popular Programming Libraries [from " + model.toString() + "] in " + language + " for accessing LLMs:");
        return libraries;
    }

    record LibraryResult(String llmName, Libraries response) {
        @Override
        public String toString() {
            return "--- " +
                   llmName +
                   " ---\n" +
                   response +
                   "\n\n";
        }
    }

    @StructuredPrompt("""
            Summarize these LLM responses about {{language}} LLM libraries in one markdown table,
            with a column per provider:
            {{results}}
            """)
    record ConsolidatePrompt(String language, List<LibraryResult> results) {
    }

    interface ConsolidateService {
        @dev.langchain4j.service.SystemMessage("You are an ai developer expert.")
        String consolidate(@dev.langchain4j.service.UserMessage ConsolidatePrompt prompt);
    }

    private static String consolidate(ChatModel model, String language, List<LibraryResult> llmResults) {
        ConsolidateService service = AiServices.builder(ConsolidateService.class)
                .chatModel(model)
                .build();

        var prompt = new ConsolidatePrompt(language, llmResults);
        return service.consolidate(prompt);
    }

    static void main() throws Exception {
        var numberOfItems = 5;
        var language = "Python";

        // Fan out: one virtual thread per LLM call — cheap blocking, no thread pool exhaustion
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<LibraryResult> claudeFuture = executor.submit(() ->
                    new LibraryResult("claude", getLLMLibraries(CLAUDE, language, numberOfItems)));

            Future<LibraryResult> openaiFuture = executor.submit(() ->
                    new LibraryResult("openai", getLLMLibraries(OPENAI, language, numberOfItems)));

            Future<LibraryResult> geminiFuture = executor.submit(() ->
                    new LibraryResult("gemini", getLLMLibraries(GEMINI, language, numberOfItems)));

            // Blocks until each result is ready; executor auto-closes after try block
            List<LibraryResult> results = List.of(
                    claudeFuture.get(),
                    openaiFuture.get(),
                    geminiFuture.get()
            );

            println("All results collected. Now consolidating...");

            String consolidatedResult = consolidate(CLAUDE, language, results);
            writeToFile(consolidatedResult, "llm_libraries_vt.md");
        }
    }
}
