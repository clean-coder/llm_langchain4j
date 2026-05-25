package parallel.service;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.service.AiServices;

import java.util.List;

public class ConsolidateLibrariesService {

    @StructuredPrompt("""
            Summarize these LLM responses about {{language}} LLM libraries in one markdown table,
            with a column per provider:
            {{results}}
            """)
    record ConsolidatePrompt(String language, List<LibraryResult> results) {
    }

    interface ConsolidateService {
        @dev.langchain4j.service.SystemMessage("You are an ai developer expert.")
        String consolidate(@dev.langchain4j.service.UserMessage ConsolidatePrompt prompt);
    }

    public static String consolidate(ChatModel model, String language, List<LibraryResult> llmResults) {
        ConsolidateService service = AiServices.builder(ConsolidateService.class)
                .chatModel(model)
                .build();

        var prompt = new ConsolidatePrompt(language, llmResults);
        return service.consolidate(prompt);
    }
}
