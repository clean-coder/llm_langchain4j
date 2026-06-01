package fundamentals;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import static util.ResponseHelper.printRequestResponseInfo;

public class RequestOpenAI {
    private static final OpenAiChatModelName MODEL_NAME = OpenAiChatModelName.GPT_4_1_NANO;

    void simpleQuery(String prompt) {
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(MODEL_NAME)
                .logRequests(true)
                .logResponses(true)
                .build();

        String response = model.chat(prompt);
        printRequestResponseInfo(prompt, MODEL_NAME.name(), response);
    }

    void main() {
        var prompt = "What is LangChain4j? Please answer in max 1 sentences.";
        new RequestOpenAI().simpleQuery(prompt);
    }
}