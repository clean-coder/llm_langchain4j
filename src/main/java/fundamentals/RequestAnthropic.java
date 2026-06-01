package fundamentals;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;

import static java.lang.IO.println;
import static util.ResponseHelper.*;

public class RequestAnthropic {
    private static final AnthropicChatModelName MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;

    void simpleQuery(String prompt) {
        ChatModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                .build();

        String response = model.chat(prompt);
        printRequestResponseInfo(prompt, MODEL_NAME.name(), response);
    }

    /*
    needed setup:
    * mvn dependency to ch.qos.logback
    * config logging in: logback.xml
     */
    void simpleQueryWithLogging(String prompt) {
        ChatModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                .logRequests(true)
                .logResponses(true)
                .build();

        String response = model.chat(prompt);
        printRequestResponseInfo(prompt, MODEL_NAME.name(), response);
    }


    void simpleQueryWithSystemMessage(String prompt) {
        ChatModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                .build();

        SystemMessage systemMessage = SystemMessage.from("You are a software developer expert.");
        UserMessage userMessage = UserMessage.from(prompt);

        ChatResponse response = model.chat(systemMessage, userMessage);
        printRequestResponseInfo(prompt, MODEL_NAME.name(), response.aiMessage().text());
    }


    void simpleQueryWithResponseMetaData(String prompt) {
        ChatModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(MODEL_NAME)
                .build();

        UserMessage userMessage = UserMessage.from(prompt);
        ChatResponse response = model.chat(userMessage);

        String content = response.aiMessage().text();
        println("Response with Metadata:");
        println("Content: " + content);

        printTokenUsage(response);
        printFinishReason(response);
    }

    void main() {
        var prompt = "What is LangChain4j? Please answer in 1 sentence.";
        var claude = new RequestAnthropic();
        claude.simpleQuery(prompt);
        claude.simpleQueryWithSystemMessage(prompt);
        claude.simpleQueryWithLogging(prompt);
        claude.simpleQueryWithResponseMetaData(prompt);
    }
}