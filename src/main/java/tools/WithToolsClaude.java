package tools;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;

import static java.lang.IO.print;
import static util.ResponseHelper.printRequestResponseInfo;

// works fine without a system prompt.
public class WithToolsClaude {
    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;
    private ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

    /**
     * Assistant interface for AI services with tool integration.
     */
    interface Assistant {
        String chat(String message);
    }

    void chatWithTools(String prompt) {
        ChatModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                //.logRequests(true)
                .build();

        // create assistant with WeatherTool
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(memory)
                .tools(new WeatherTool())
                .build();

        String response = assistant.chat(prompt);
        printRequestResponseInfo(prompt, MODEL_NAME.name(), response);
    }

    void main() {
        print("=== Claude with Tools Example ===");
        var claude = new WithToolsClaude();
        claude.chatWithTools("What kind of clothes do I need for a short trip to Paris?");
        claude.chatWithTools("And for London?");
    }
}