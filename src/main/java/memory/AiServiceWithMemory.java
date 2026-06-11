package memory;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;

import static java.lang.IO.println;

public class AiServiceWithMemory {
    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    /**
     * Interface for AI service with memory integration.
     * This demonstrates how to create persistent conversational AI.
     */
    interface AssistantWithMemory {
        String chat(String message);
    }

    private final ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

    void chatWithMemory(String prompt) {
        ChatModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                .logRequests(true)
                .build();

        // create AI service with memory integration
        AssistantWithMemory assistant = AiServices.builder(AssistantWithMemory.class)
                .chatModel(model)
                .chatMemory(memory)
                .build();

        String response = assistant.chat(prompt);
        print(prompt, response);
    }

    private void print(String query, String response) {
        println("Query: " + query);
        println("Response: " + response);
        println("-".repeat(50));
    }

    void main() {
        var claude = new AiServiceWithMemory();
        claude.chatWithMemory("What is the capital of France.");
        claude.chatWithMemory("And Sweden.");
    }
}