package memory;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;

import static java.lang.IO.println;

public class NoMemory {
    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    void chat(String prompt) {
        ChatModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                .build();

        String response = model.chat(prompt);
        print(prompt, response);
    }

    private void print(String query, String response) {
        println("Query: " + query);
        println("Response: " + response);
        println("-----------------------------");

    }

    void main() {
        var claude = new NoMemory();
        claude.chat("What is the capital of France.");
        claude.chat("And Sweden.");
    }
}