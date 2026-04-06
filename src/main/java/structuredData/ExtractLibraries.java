package structuredData;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_NANO;
import static java.lang.IO.println;

public class ExtractLibraries {
    interface LibraryService {
        // simple system prompt: works fine for OpenAI, but not for Claude
        //@SystemMessage("You are an ai developer expert.")

        // extended system prompt: works for OpenAI and Claude
        @SystemMessage("You are an ai developer expert. Return ONLY raw JSON with no markdown, no code fences, no explanation. Do not wrap the response in ```json or ``` blocks.")
        Libraries getLibraries(@UserMessage String query);
    }

    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    public void extractLLMLibrariesWithAiService(String language, int noOfItems) {
        /*
        Problem: Claude is returning the JSON wrapped in markdown code fences (```json ... ```),
        while OpenAI returns plain JSON. This causes parsing issues when we try to map the response to our Libraries record.
        Fix: extended SystemMessage
         */
        ChatModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                .build();

        ChatModel _model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        LibraryService service = AiServices.builder(LibraryService.class)
                .chatModel(model)
                .build();

        var query = "What are the " + noOfItems
                    + " most popular programming libraries in " + language
                    + " for accessing LLMs?";
        Libraries libraries = service.getLibraries(query);

        println("Popular Programming Libraries in " + language + " for accessing LLMs:");
        println(libraries);
    }

    static void main() {
        var extractor = new ExtractLibraries();
        extractor.extractLLMLibrariesWithAiService("Python", 5);
        extractor.extractLLMLibrariesWithAiService("Java", 5);
    }
}
