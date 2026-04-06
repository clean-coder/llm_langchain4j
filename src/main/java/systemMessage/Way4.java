package systemMessage;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import static util.ResponseHelper.printRequestResponseInfo;

// dynamic system prompt with variables
public class Way4 {

    interface Assistant {
        @SystemMessage("You are a {{style}} assistant.")
        String chat(@V("style") String style, @UserMessage String userMessage);
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

        var style1 = "pirate";
        String response1 = assistant.chat(style1, query);
        printRequestResponseInfo(query + " [" + style1 + "]", MODEL_NAME.name(), response1);

        var style2 = "children teacher";
        String response2 = assistant.chat(style2, query);
        printRequestResponseInfo(query + " [" + style2 + "]", MODEL_NAME.name(), response2);
    }
}
