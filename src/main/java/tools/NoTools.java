package tools;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;

import static util.ResponseHelper.*;

public class NoTools {
    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    void chat(String prompt) {
        ChatModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                .build();

        String response = model.chat(prompt);
        printRequestResponseInfo(prompt, MODEL_NAME.name(), response);
    }

    void main() {
        var query = "What kind of clothes do I need for a short trip to Paris?";
        var claude = new NoTools();
        claude.chat(query);
    }
}