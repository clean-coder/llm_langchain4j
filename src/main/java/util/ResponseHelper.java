package util;

import dev.langchain4j.model.chat.response.ChatResponse;

import static java.lang.IO.println;

public class ResponseHelper {
    public static void printRequestResponseInfo(String query, String model, String response) {
        println("query: " + query);
        println("model: " + model);
        println(response);
        print_line();
    }

    public static void printRequestResponseInfo(String query, String model, ChatResponse response) {
        printRequestResponseInfo(query, model, response.aiMessage().text());
    }

    public static void printTokenUsage(ChatResponse response) {
        if (response.tokenUsage() != null) {
            println("Token Usage: " + response.tokenUsage());
            println("  Input Tokens : " + response.tokenUsage().inputTokenCount());
            println("  Output Tokens: " + response.tokenUsage().outputTokenCount());
            println("  Total Tokens : " + response.tokenUsage().totalTokenCount());
        } else {
            println("Token Usage: Not available");
        }
        print_line();
    }

    public static void printFinishReason(ChatResponse response) {
        if (response.finishReason() != null) {
            println("Finish Reason: " + response.finishReason());
        } else {
            println("Finish Reason: Not available");
        }
        print_line();
    }

    private static void print_line() {
        println("=".repeat(50));
    }

}
