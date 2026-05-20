package prompt;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;

import static util.ResponseHelper.printRequestResponseInfo;

// manual string concatenation: not recommended
// fragile, hard to reuse, messy to maintain.
public class StringConcatExample {
    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    private static String createPrompt(String text) {
        return "Translate the following text to french:" + text;
    }

    void main() {
        ChatModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                .build();

        String promptHello = createPrompt("Hello World");
        String response = model.chat(promptHello);
        printRequestResponseInfo(promptHello, MODEL_NAME.name(), response);

        String promptBye = createPrompt("Bye World");
        String responseBye = model.chat(promptBye);
        printRequestResponseInfo(promptBye, MODEL_NAME.name(), responseBye);
    }
}

