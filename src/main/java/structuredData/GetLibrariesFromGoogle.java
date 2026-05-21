package structuredData;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_NANO;
import static java.lang.IO.println;

public class GetLibrariesFromGoogle {

    interface LibraryService {
        @SystemMessage("You are an ai developer expert.")
        Libraries getLibraries(@UserMessage LibraryPrompt query);
    }

    @StructuredPrompt("What are the {{numOfItems}} most popular programming libraries in {{language}} for accessing LLMs?")
    record LibraryPrompt(int numOfItems, String language) {
    }

    public void getLLMLibrariesWithAiService(String language, int noOfItems) {
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_1_NANO)
                .build();

        LibraryService service = AiServices.builder(LibraryService.class)
                .chatModel(model)
                .build();

        var prompt = new LibraryPrompt(noOfItems, language);
        Libraries libraries = service.getLibraries(prompt);

        println("Popular Programming Libraries [from Google] in " + language + " for accessing LLMs:");
        println(libraries);
    }

    static void main() {
        var extractor = new GetLibrariesFromGoogle();
        extractor.getLLMLibrariesWithAiService("Python", 5);
        println("--------------------------------------------------");
        extractor.getLLMLibrariesWithAiService("Java", 5);
    }
}
