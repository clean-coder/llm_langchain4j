package systemMessage;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;

import java.util.List;

import static util.ResponseHelper.printRequestResponseInfo;

// using SystemMessage directly (low-level API)
public class Way1 {

    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    private static ChatModel model = AnthropicChatModel.builder()
            .apiKey(System.getenv("ANTHROPIC_API_KEY"))
            .modelName(MODEL_NAME)
            .build();

    static void main() {
        UserMessage userMessage = UserMessage.from("Tell me about the weather");
        SystemMessage systemMessage = SystemMessage.from("You are a pirate assistant.");

        ChatResponse response = model.chat(List.of(systemMessage, userMessage));
        printRequestResponseInfo(userMessage.singleText(), MODEL_NAME.name(), response.toString());
    }
}
