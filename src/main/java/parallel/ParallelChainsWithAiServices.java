package parallel;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.service.AiServices;
import structuredData.Libraries;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.IO.println;
import static parallel.helper.FileHelper.writeToFile;
import static parallel.helper.ModelHelper.*;

public class ParallelChainsWithAiServices {

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

        // Fan out: run all three in parallel (equivalent to RunnableParallel)
        CompletableFuture<LibraryResult> claudeFuture = CompletableFuture.supplyAsync(() ->
                new LibraryResult("claude", getLLMLibraries(CLAUDE, language, numberOfItems)));

        CompletableFuture<LibraryResult> openaiFuture = CompletableFuture.supplyAsync(() ->
                new LibraryResult("openai", getLLMLibraries(OPENAI, language, numberOfItems)));

        CompletableFuture<LibraryResult> geminiFuture = CompletableFuture.supplyAsync(() ->
                new LibraryResult("gemini", getLLMLibraries(GEMINI, language, numberOfItems)));

        // Collect all results (blocks until all three complete)
        CompletableFuture.allOf(claudeFuture, openaiFuture, geminiFuture).join();

        List<LibraryResult> results = new ArrayList<>();
        results.add(new LibraryResult(claudeFuture.get().llmName(), claudeFuture.get().response()));
        results.add(new LibraryResult(openaiFuture.get().llmName(), openaiFuture.get().response()));
        results.add(new LibraryResult(geminiFuture.get().llmName(), geminiFuture.get().response()));

        println("All results collected. Now consolidating...");

        // Consolidate with a single Claude call
        String consolidatedResult = consolidate(CLAUDE, language, results);
        writeToFile(consolidatedResult, "llm_libraries.md");
    }
}
