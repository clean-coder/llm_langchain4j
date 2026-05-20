package prompt;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;

public class StructuredPromptWithAiServiceExample {

    @StructuredPrompt("Tell me about {{topic}} in {{language}}. Be concise.")
    record MyPrompt(String topic, String language) {
    }

    interface AssistantService {
        String ask(@UserMessage MyPrompt prompt);
    }

    static void main(String[] args) {

        var model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(AnthropicChatModelName.CLAUDE_SONNET_4_6)
                .build();

        AssistantService service = AiServices.builder(AssistantService.class)
                .chatModel(model)
                .build();

        // Use the structured prompt
        var prompt = new MyPrompt("quantum computing", "French");
        String response = service.ask(prompt);

        System.out.println(response);
    }
}
