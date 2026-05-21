package structuredData;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import static java.lang.IO.println;

public class GetLibrariesFromAnthropic {
    interface LibraryService {
        @SystemMessage("You are an ai developer expert.")
        Libraries getLibraries(@UserMessage LibraryPrompt query);
    }

    @StructuredPrompt("What are the {{numOfItems}} most popular programming libraries in {{language}} for accessing LLMs?")
    record LibraryPrompt(int numOfItems, String language) {
    }

    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    public void getLLMLibrariesWithAiService(String language, int noOfItems) {
        ChatModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                .build();

        LibraryService service = AiServices.builder(LibraryService.class)
                .chatModel(model)
                .build();

        var prompt = new LibraryPrompt(noOfItems, language);
        Libraries libraries = service.getLibraries(prompt);

        println("Popular Programming Libraries [from Anthropic] in " + language + " for accessing LLMs:");
        println(libraries);
    }

    static void main() {
        var extractor = new GetLibrariesFromAnthropic();
        extractor.getLLMLibrariesWithAiService("Python", 5);
        println("--------------------------------------------------");
        extractor.getLLMLibrariesWithAiService("Java", 5);
    }
}
