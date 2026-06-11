package tools;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

import static java.lang.IO.print;
import static util.ResponseHelper.printRequestResponseInfo;

// needs a system prompt. otherwise the model will not use the tool and give generic packing advice without checking the weather forecast first.
public class WithToolsOpenAI {
    private static final OpenAiChatModelName MODEL_NAME = OpenAiChatModelName.GPT_5_MINI;
    private final ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

    private static final String SYSTEM_PROMPT = """
            You are a helpful travel assistant.
            
            IMPORTANT: When users ask about:
            - What to pack for a trip
            - What clothes to bring
            - Weather conditions
            - Temperature in a city
            
            You MUST use the getForecast tool to check the current weather before providing advice. Never give generic packing advice without checking the actual weather forecast first.
            """;

    /**
     * Assistant interface for AI services with tool integration.
     */
    interface Assistant {
        @SystemMessage(SYSTEM_PROMPT)
        String chat(String message);
    }

    void chatWithTools(String prompt) {
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(MODEL_NAME)
                //.logRequests(true)
                //.logResponses(true)
                .build();

        // Create assistant with WeatherTool
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(memory)
                .tools(new WeatherTool())
                .build();

        String response = assistant.chat(prompt);
        printRequestResponseInfo(prompt, MODEL_NAME.name(), response);
    }

    void main() {
        print("=== OpenAI with Tools Example ===");
        var claude = new WithToolsOpenAI();
        claude.chatWithTools("What kind of clothes do I need for a short trip to Paris?");
        //claude.chatWithTools("And for London?");
    }
}