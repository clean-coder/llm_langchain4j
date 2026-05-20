package prompt;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;

import static util.ResponseHelper.printRequestResponseInfo;

// the recommended approach: the prompt as a proper domain object.
// (define the template as an annotation on a class, and the variables become fields)
public class StructuredPromptExample {

    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    static void main() {
        @StructuredPrompt("Translate the following text to {{language}}: {{text}}")
        record TranslationPrompt(String language, String text) {}

        ChatModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                .build();

        Prompt promptFrench = StructuredPromptProcessor.toPrompt(
                new TranslationPrompt("French", "Hello, world!")
        );
        ChatResponse responseFrench = model.chat(promptFrench.toUserMessage());
        printRequestResponseInfo(promptFrench.text(), MODEL_NAME.name(), responseFrench);

        Prompt promptGerman = StructuredPromptProcessor.toPrompt(
                new TranslationPrompt("German", "Hello, world!")
        );
        ChatResponse responseGerman = model.chat(promptGerman.toUserMessage());
        printRequestResponseInfo(promptGerman.text(), MODEL_NAME.name(), responseGerman);
    }
}
