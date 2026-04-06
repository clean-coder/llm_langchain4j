package parallel;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.IO.println;

///
/// LangChain4j is fundamentally different from LangChain — not just in API design, but in mindset.
/// LangChain's evolution went from Chains → Runnables → StateGraphs, while LangChain4j began with
/// Chains but has since leaned fully into a Java-native, interface-heavy, strongly typed design.
/// Medium There is no direct RunnableParallel equivalent in LangChain4j — but you can absolutely
/// achieve the same pattern using standard Java concurrency tools. Here's how:
///
/// The Java / LangChain4j Approach
/// The idiomatic way to replicate RunnableParallel in Java is with CompletableFuture, which gives
/// you the same concurrent fan-out → collect → consolidate flow:
///
public class ParallelChains {

    record LibraryResult(String llmName, String response) {
    }

    private static String buildConsolidationPrompt(List<LibraryResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("Summarize these LLM responses about Python LLM libraries in one markdown table, ");
        sb.append("with a column per provider:\n\n");
        results.forEach(result -> sb.append(formatSingleLLMResult(result)));
        return sb.toString();
    }

    private static String formatSingleLLMResult(LibraryResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- ")
                .append(result.llmName)
                .append(" ---\n")
                .append(result.response)
                .append("\n\n");
        return sb.toString();
    }

    private static String invoke(ChatModel model, String system, String user) {
        return model.chat(SystemMessage.from(system), UserMessage.from(user)).aiMessage().text();
    }

    private static void writeToFile(String content) {
        Path path = Paths.get("output.md");
        try {
            Files.writeString(path, content);
            System.out.println("File written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void main(String[] args) throws Exception {
        // 1 — Set up the three models
        ChatModel claude = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName("claude-sonnet-4-5-20250929")
                .temperature(0.3)
                .build();

        ChatModel openai = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-4o-mini")
                .temperature(0.3)
                .build();

        ChatModel gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("GEMINI_API_KEY"))
                .modelName("gemini-2.5-flash-lite")
                .temperature(0.3)
                .build();

        String systemPrompt = "What are the most popular programming libraries for accessing LLMs?";
        String userPrompt = "Programming Language: Python, Number of Libraries: 5. " +
                            "Reply as JSON: {\"libraries\": [{\"name\",\"provider\",\"url\",\"language\",\"version\"}]}";

        // 2 — Fan out: run all three in parallel (equivalent to RunnableParallel)
        CompletableFuture<LibraryResult> claudeFuture = CompletableFuture.supplyAsync(() ->
                new LibraryResult("claude", invoke(claude, systemPrompt, userPrompt)));

        CompletableFuture<LibraryResult> openaiFuture = CompletableFuture.supplyAsync(() ->
                new LibraryResult("openai", invoke(openai, systemPrompt, userPrompt)));

        CompletableFuture<LibraryResult> geminiFuture = CompletableFuture.supplyAsync(() ->
                new LibraryResult("gemini", invoke(gemini, systemPrompt, userPrompt)));

        // 3 — Collect all results (blocks until all three complete)
        CompletableFuture.allOf(claudeFuture, openaiFuture, geminiFuture).join();

        List<LibraryResult> results = new ArrayList<>();
        results.add(new LibraryResult(claudeFuture.get().llmName(), claudeFuture.get().response()));
        results.add(new LibraryResult(openaiFuture.get().llmName(), openaiFuture.get().response()));
        results.add(new LibraryResult(geminiFuture.get().llmName(), geminiFuture.get().response()));

        // 4 — Consolidate with a single Claude call (same as your Python step 2)
        String consolidationPrompt = buildConsolidationPrompt(results);
        println("Consolidation Prompt:\n" + consolidationPrompt);
        String consolidated = invoke(claude, "You are a helpful assistant.", consolidationPrompt);

        //println(consolidated);
        writeToFile(consolidated);
    }
}
