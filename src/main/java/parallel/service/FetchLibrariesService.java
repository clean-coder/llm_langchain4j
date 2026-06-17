package parallel.service;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import parallel.dataStructure.Libraries;

import static java.lang.IO.println;

public class FetchLibrariesService {

    @StructuredPrompt("""
            What are the {{numOfItems}} most popular programming libraries in {{language}} for accessing LLMs?
            Reply with name, provider, url, language, version for each library""")
    record LibraryPrompt(int numOfItems, String language) {
    }

    interface LibraryService {
        @SystemMessage("You are an ai developer expert.")
        Libraries getLibraries(@UserMessage LibraryPrompt prompt);
    }

    public static Libraries getLLMLibraries(ChatModel model, String language, int noOfItems) {
        LibraryService service = AiServices.builder(LibraryService.class)
                .chatModel(model)
                .build();

        var prompt = new LibraryPrompt(noOfItems, language);
        Libraries libraries = service.getLibraries(prompt);

        println("Popular Programming Libraries [from " + model.toString() + "] in " + language + " for accessing LLMs:");
        return libraries;
    }
}
