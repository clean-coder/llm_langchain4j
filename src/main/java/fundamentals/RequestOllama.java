package fundamentals;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

import static util.ResponseHelper.printRequestResponseInfo;

public class RequestOllama {
    private static final String OLLAM_MODEL_NAME = "llama3.1";
    public static final String OLLAMA_BASE_URL = "http://localhost:11434";

    void simpleQuery(String query) {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl(OLLAMA_BASE_URL)
                .modelName(OLLAM_MODEL_NAME)
                .build();

        String response = model.chat(query);
        printRequestResponseInfo(query, OLLAM_MODEL_NAME, response);
    }

    void main() {
        var query = "What is LangChain4j? Please answer in max 1 sentences.";
        new RequestOllama().simpleQuery(query);
    }
}