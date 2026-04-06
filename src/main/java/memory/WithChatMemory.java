package memory;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;

import static java.lang.IO.println;

public class WithChatMemory {
    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    // Create chat memory with a window of 10 messages
    private ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

    void chatWithChatMemory(String query) {
        ChatModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                .logRequests(true)
                .build();

        UserMessage firstMessage = UserMessage.from(query);
        memory.add(firstMessage);

        ChatResponse response = model.chat(memory.messages());
        memory.add(response.aiMessage());

        print(query, response);
    }

    private void print(String query, ChatResponse response) {
        println("Query: " + query);
        println("Response: " + response.aiMessage().text());
        println("-".repeat(50));
    }

    void main() {
        var claude = new WithChatMemory();
        claude.chatWithChatMemory("What is the capital of France.");
        claude.chatWithChatMemory("And Sweden.");
    }
}