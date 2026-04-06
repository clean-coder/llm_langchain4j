package systemMessage;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;

import static util.ResponseHelper.printRequestResponseInfo;

// using ChatMemory with a system message
public class Way2 {

    interface Assistant {
        String chat(String userMessage);
    }

    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    private static ChatModel model = AnthropicChatModel.builder()
            .apiKey(System.getenv("ANTHROPIC_API_KEY"))
            .modelName(MODEL_NAME)
            .build();

    static void main() {
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        chatMemory.add(SystemMessage.from("You are a pirate assistant."));

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(chatMemory)
                .build();

        var query = "Tell me about the weather";
        String response = assistant.chat(query);
        printRequestResponseInfo(query, MODEL_NAME.name(), response);
    }
}
