package parallel;

import parallel.dataStructure.LibraryResult;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.IO.println;
import static parallel.helper.FileHelper.writeToFile;
import static parallel.helper.ModelHelper.*;
import static parallel.service.ConsolidateLibrariesService.consolidate;
import static parallel.service.FetchLibrariesService.getLLMLibraries;

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

    static void main() throws Exception {
        var numberOfItems = 5;
        var language = "Python";

        // Fan out: one virtual thread per LLM call — cheap blocking, no thread pool exhaustion
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var claudeFuture = executor.submit(() ->
                    new LibraryResult("claude", getLLMLibraries(CLAUDE, language, numberOfItems)));

            var openaiFuture = executor.submit(() ->
                    new LibraryResult("openai", getLLMLibraries(OPENAI, language, numberOfItems)));

            var geminiFuture = executor.submit(() ->
                    new LibraryResult("gemini", getLLMLibraries(GOOGLE, language, numberOfItems)));

            // Blocks until each result is ready; executor auto-closes after try block
            var results = List.of(
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
