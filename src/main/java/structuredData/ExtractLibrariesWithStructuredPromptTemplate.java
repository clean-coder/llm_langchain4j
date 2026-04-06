package structuredData;

import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import static java.lang.IO.println;

public class ExtractLibrariesWithStructuredPromptTemplate {

    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    interface LibraryService {
        @SystemMessage("You are an ai developer expert. Return ONLY raw JSON with no markdown, no code fences, no explanation. Do not wrap the response in ```json or ``` blocks.")
        Libraries getLibraries(@UserMessage String query);
    }

    @StructuredPrompt("What are the {{numOfItems}} most popular programming libraries in {{language}} for accessing LLMs?")
    static class CreateLibraryPrompt {
        private int numOfItems;
        private String language;
    }

    void extractLLMLibrariesWithAiService(CreateLibraryPrompt createLibraryPrompt) {
        ChatModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                .build();

        LibraryService service = AiServices.builder(LibraryService.class)
                .chatModel(model)
                .build();

        Prompt prompt = StructuredPromptProcessor.toPrompt(createLibraryPrompt);
        Libraries actorFilms = service.getLibraries(prompt.toUserMessage().toString());

        println(">> " + extractQueryFromPrompt(prompt));
        println(actorFilms);
        println("--------------------------------------------------");
    }

    private String extractQueryFromPrompt(Prompt prompt) {
        Content content = prompt.toUserMessage().contents().get(0);
        if (content instanceof TextContent textContent) {
            return textContent.text();
        }
        return "query extraction failed";
    }

    static void main() {
        var extractor = new ExtractLibrariesWithStructuredPromptTemplate();
        CreateLibraryPrompt createLibraryPrompt = new CreateLibraryPrompt();

        createLibraryPrompt.numOfItems = 5;
        createLibraryPrompt.language = "Python";
        extractor.extractLLMLibrariesWithAiService(createLibraryPrompt);

        createLibraryPrompt.numOfItems = 3;
        createLibraryPrompt.language = "Java";
        extractor.extractLLMLibrariesWithAiService(createLibraryPrompt);
    }
}
