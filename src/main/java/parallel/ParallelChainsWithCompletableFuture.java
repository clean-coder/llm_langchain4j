package parallel;

import parallel.dataStructure.LibraryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.IO.println;
import static parallel.helper.FileHelper.writeToFile;
import static parallel.helper.ModelHelper.*;
import static parallel.service.ConsolidateLibrariesService.consolidate;
import static parallel.service.FetchLibrariesService.getLLMLibraries;

public class ParallelChainsWithCompletableFuture {

    static void main() throws Exception {
        var numberOfItems = 5;
        var language = "Python";

        // Fan out: run all three in parallel (equivalent to RunnableParallel in LangChain)
        CompletableFuture<LibraryResult> claudeFuture = CompletableFuture.supplyAsync(() ->
                new LibraryResult("claude", getLLMLibraries(CLAUDE, language, numberOfItems)));

        CompletableFuture<LibraryResult> openaiFuture = CompletableFuture.supplyAsync(() ->
                new LibraryResult("openai", getLLMLibraries(OPENAI, language, numberOfItems)));

        CompletableFuture<LibraryResult> geminiFuture = CompletableFuture.supplyAsync(() ->
                new LibraryResult("gemini", getLLMLibraries(GOOGLE, language, numberOfItems)));

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
