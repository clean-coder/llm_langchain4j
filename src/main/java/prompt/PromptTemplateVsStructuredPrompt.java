package prompt;

import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.input.structured.StructuredPrompt;

import java.util.Map;

/*
A rough analogy:

PromptTemplate = a string with holes to fill
StructuredPrompt = a typed DTO that is the prompt

 */
public class PromptTemplateVsStructuredPrompt {
    static void main() {
        /*
        PromptTemplate is a simple string template with placeholders that gets filled in at runtime.
        It's purely about text interpolation — you define a template like "Tell me about {{topic}}"
        and inject variables into it. It has no knowledge of the expected output structure.
         */
        // PromptTemplate — manual variable binding
        PromptTemplate template = PromptTemplate.from("Tell me about {{topic}} in {{language}}");
        Prompt prompt = template.apply(Map.of("topic", "AI", "language", "French"));
        System.out.println(prompt.text()); // Tell me about AI in French

        /*
        StructuredPrompt is a higher-level concept — it's an annotation you put on a class or
        record, where the class fields are the template variables. It combines the prompt template
        and the input data structure into one object. So instead of passing loose variables, you pass
        a typed object that carries both the prompt pattern and the data.

        So StructuredPrompt is essentially PromptTemplate with the added benefit of type
        safety and cleaner integration with AiServices. For simple one-off prompts,
        PromptTemplate is fine. For reusable, structured inputs passed through AiServices,
        StructuredPrompt is the cleaner approach.
         */

        // StructuredPrompt — variables come from the object fields
        // (automatic variable binding via reflection)
        @StructuredPrompt("Tell me about {{topic}} in {{language}}")
        record MyPrompt(String topic, String language) {
        }

        MyPrompt myPrompt = new MyPrompt("AI", "French");
        System.out.println(myPrompt); // MyPrompt[topic=AI, language=French]

        // Then used cleanly in AiServices
        // FIXME
        //service.ask(new MyPrompt("AI", "French"));
    }
}
