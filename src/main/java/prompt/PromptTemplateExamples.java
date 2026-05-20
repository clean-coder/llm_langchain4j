package prompt;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.Map;

import static util.ResponseHelper.printRequestResponseInfo;

public class PromptTemplateExamples {

    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    // PromptTemplate with default variable name "it". Simple to use.
    static class PromptTemplateWithDefaultVariableName {

        void main() {
            var stringTemplate = "Translate the following text to french: {{it}}";
            PromptTemplate promptTemplate = PromptTemplate.from(stringTemplate);

            ChatModel model = AnthropicChatModel.builder()
                    .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                    .modelName(MODEL_NAME)
                    .build();

            Prompt promptHello = promptTemplate.apply("Hello World");
            ChatResponse responseHello = model.chat(promptHello.toUserMessage());
            printRequestResponseInfo(promptHello.text(), MODEL_NAME.name(), responseHello);

            Prompt promptBye = promptTemplate.apply("Bye World");
            ChatResponse responseBye = model.chat(promptBye.toUserMessage());
            printRequestResponseInfo(promptBye.text(), MODEL_NAME.name(), responseBye);
        }
    }

    // PromptTemplate with a user defined variable name. More flexible and reusable.
    static class PromptTemplateWithNamedVariable {

        void main() {
            var stringTemplate = "Translate the following text to french: {{text}}";
            PromptTemplate promptTemplate = PromptTemplate.from(stringTemplate);

            ChatModel model = AnthropicChatModel.builder()
                    .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                    .modelName(MODEL_NAME)
                    .build();

            Prompt promptHello = promptTemplate.apply(Map.of(
                    "text", "Hello World")
            );
            ChatResponse responseHello = model.chat(promptHello.toUserMessage());
            printRequestResponseInfo(promptHello.text(), MODEL_NAME.name(), responseHello);

            Prompt promptBye = promptTemplate.apply(Map.of(
                    "text", "Bye World")
            );
            ChatResponse responseBye = model.chat(promptBye.toUserMessage());
            printRequestResponseInfo(promptBye.text(), MODEL_NAME.name(), responseBye);
        }
    }

    // PromptTemplate with 2 user defined variable names (text and language).
    static class PromptTemplateWithMultipleVariables {

        void main() {
            var stringTemplate = "Translate the following text to {{language}}: {{text}}";
            PromptTemplate promptTemplate = PromptTemplate.from(stringTemplate);

            ChatModel model = AnthropicChatModel.builder()
                    .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                    .modelName(MODEL_NAME)
                    .build();

            Prompt prompt = promptTemplate.apply(Map.of(
                    "text", "Hello World",
                    "language", "French"
            ));

            ChatResponse response = model.chat(prompt.toUserMessage());
            printRequestResponseInfo(prompt.text(), MODEL_NAME.name(), response);
        }
    }
}