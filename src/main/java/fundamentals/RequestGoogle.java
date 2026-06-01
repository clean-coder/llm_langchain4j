package fundamentals;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

import static util.ResponseHelper.printRequestResponseInfo;

public class RequestGoogle {
    private static final String MODEL_NAME = "gemini-2.5-flash-lite";

    void simpleQuery(String prompt) {
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("GEMINI_API_KEY"))
                .modelName(MODEL_NAME)
                .build();

        String response = model.chat(prompt);
        printRequestResponseInfo(prompt, MODEL_NAME, response);
    }

    void main() {
        var prompt = "What is LangChain4j? Please answer in max 1 sentences.";
        new RequestGoogle().simpleQuery(prompt);
    }
}