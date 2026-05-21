package aiService;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;

import static java.lang.IO.println;

public class AiServiceExample {
    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    static void main() {

        ChatModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                .build();

        @StructuredPrompt("Translate the following text to {{language}}: {{text}}")
        record TranslationPrompt(String language, String text) {
        }

        interface AssistantService {
            String translate(@UserMessage TranslationPrompt prompt);
        }


        AssistantService assistant = AiServices.create(AssistantService.class, model);

        String replyFrench = assistant.translate(
                new TranslationPrompt("french", "Hello World!")
        );
        println(replyFrench);

        String replyGerman = assistant.translate(
                new TranslationPrompt("german", "Hello World!")
        );
        println(replyGerman);
    }
}
