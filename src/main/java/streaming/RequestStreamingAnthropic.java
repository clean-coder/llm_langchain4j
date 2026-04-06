package streaming;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static java.lang.IO.println;
import static util.ResponseHelper.printFinishReason;
import static util.ResponseHelper.printRequestResponseInfo;

public class RequestStreamingAnthropic {
    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    void streamingQuery(String query) throws InterruptedException {
        StreamingChatModel model = AnthropicStreamingChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                .build();

        var latch = new CountDownLatch(1);
        var fullResponse = new StringBuilder();

        model.chat(query, new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String token) {
                println(token);
                fullResponse.append(token);
            }

            @Override
            public void onCompleteResponse(ChatResponse response) {
                println("\n" + "=".repeat(50));
                println("Streaming completed!");
                println("Full response length: " + fullResponse.length() + " characters");
                printRequestResponseInfo(query, MODEL_NAME.name(), fullResponse.toString());
                printFinishReason(response);
                latch.countDown();
            }

            @Override
            public void onError(Throwable error) {
                println("Error: " + error.getMessage());
                latch.countDown();
            }
        });
        latch.await();
    }

    void streamingQueryWithSystemMessage(String query) throws InterruptedException {
        StreamingChatModel model = AnthropicStreamingChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                .build();

        var systemMessage = SystemMessage.from("You are a software developer expert.");
        var userMessage = UserMessage.from(query);

        var latch = new CountDownLatch(1);
        var fullResponse = new StringBuilder();

        model.chat(List.of(systemMessage, userMessage), new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String token) {
                println(token);
                fullResponse.append(token);
            }

            @Override
            public void onCompleteResponse(ChatResponse response) {
                println("\n" + "=".repeat(50));
                println("Streaming completed!");
                println("Full response length: " + fullResponse.length() + " characters");
                printRequestResponseInfo(query, MODEL_NAME.name(), fullResponse.toString());
                printFinishReason(response);
                latch.countDown();
            }

            @Override
            public void onError(Throwable error) {
                println("Error: " + error.getMessage());
                latch.countDown();
            }
        });
        latch.await();
    }

    record Status(boolean error, String info) {
    }


    void streamingQueryWithErrorHandling(String query, String apiKey) throws InterruptedException {
        StreamingChatModel model = AnthropicStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(MODEL_NAME)
                .build();

        CountDownLatch latch = new CountDownLatch(1);
        var statusList = new ArrayList<Status>();

        println("\nTesting Error Handling with apiKey: " + apiKey.substring(0, 10) + "...");
        println("=".repeat(50));

        model.chat(query, new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String token) {
                println(token);
            }

            @Override
            public void onCompleteResponse(ChatResponse response) {
                println("Normal completion - no error occurred");
                latch.countDown();
            }

            @Override
            public void onError(Throwable error) {
                println("Expected error occurred: " + error.getMessage());
                statusList.add(new Status(true, error.getMessage()));
                latch.countDown();
            }
        });

        latch.await();

        if (statusList.isEmpty() || statusList.getFirst().error() == false) {
            println("No Error message captured");
        } else {
            println("Error message captured: " + statusList.getFirst().error());
            println("✅ Error handling test completed successfully");
        }
    }


    void main() throws InterruptedException {
        var query = "Why should I use LangChain4j instead of simple REST calls to access an LLM? Please answer in 1 sentence.";
        var claude = new RequestStreamingAnthropic();
        //claude.streamingQuery(query);
        //claude.streamingQueryWithSystemMessage(query);

        // normal case - no error occurred
        var validApiKey = System.getenv("ANTHROPIC_API_KEY");
        claude.streamingQueryWithErrorHandling(query, validApiKey);

        // error handling
        var invalidApiKey = "invalid-key";
        claude.streamingQueryWithErrorHandling(query, invalidApiKey);
    }
}