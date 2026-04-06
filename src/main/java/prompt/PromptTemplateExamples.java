package prompt;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.HashMap;
import java.util.Map;

import static util.ResponseHelper.printRequestResponseInfo;

// TODO
public class PromptTemplateExamples {

    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    // {{it}} ist fix!!!!
    static class PromptTemplateWithSingleVariable {

        static void main(String[] args) {
            var stringTemplate = "Give me a brief summary in one sentence about the color in {{it}}.";
            PromptTemplate promptTemplate = PromptTemplate.from(stringTemplate);

            ChatModel model = AnthropicChatModel.builder()
                    .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                    .modelName(MODEL_NAME)
                    .build();

            Prompt promptRed = promptTemplate.apply("red");
            String response = model.chat(promptRed.text());
            printRequestResponseInfo(promptRed.text(), MODEL_NAME.name(), response);

            Prompt promptBlue = promptTemplate.apply("blue");
            String responseBlue = model.chat(promptBlue.text());
            printRequestResponseInfo(promptBlue.text(), MODEL_NAME.name(), responseBlue);
        }
    }

    static class PromptTemplateWithMultipleVariables {

        static void main(String[] args) {
            var stringTemplate = "Give me a brief summary in {{numberOfSentences}} sentence about the color in {{color}}.";
            PromptTemplate promptTemplate = PromptTemplate.from(stringTemplate);

            ChatModel model = AnthropicChatModel.builder()
                    .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                    .modelName(MODEL_NAME)
                    .build();

            Map<String, Object> variables = new HashMap<>();
            variables.put("numberOfSentences", "2");
            variables.put("color", "yellow");

            // List.of() does not work
            var _variables = Map.of("numberOfSentences", "2", "color", "yellow");

            Prompt prompt = promptTemplate.apply(variables);
            String response = model.chat(prompt.text());
            printRequestResponseInfo(prompt.text(), MODEL_NAME.name(), response);
        }
    }
}