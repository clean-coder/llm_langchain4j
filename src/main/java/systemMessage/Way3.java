package systemMessage;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

import static util.ResponseHelper.printRequestResponseInfo;

// using @SystemMessage annotation with AI Services (most common)
public class Way3 {

    interface Assistant {
        @SystemMessage("You are a pirate assistant.")
        String chat(String userMessage);
    }

    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    private static ChatModel model = AnthropicChatModel.builder()
            .apiKey(System.getenv("ANTHROPIC_API_KEY"))
            .modelName(MODEL_NAME)
            .build();

    static void main() {
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .build();

        var query = "Tell me about the weather";
        String response = assistant.chat(query);
        printRequestResponseInfo(query, MODEL_NAME.name(), response);
    }
}
