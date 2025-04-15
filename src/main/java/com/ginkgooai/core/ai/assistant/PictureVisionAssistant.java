package com.ginkgooai.core.ai.assistant;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.ginkgooai.core.ai.prompt.PromptBuilder;
import com.ginkgooai.core.ai.prompt.PromptTemplate;
import com.ginkgooai.core.common.utils.ContextUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
public class PictureVisionAssistant {

    @Value("${OPEN_AI_MODEL:}")
    private String MODEL_NAME;

    private final ChatClient chatClient;


    public PictureVisionAssistant(ChatClient.Builder modelBuilder, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider) {

        // @formatter:off
        this.chatClient = modelBuilder
                .defaultSystem("""	
                         You are a customer chat support agent for "Jasper" California Renovation Master Contractor. Please reply in a friendly, helpful and pleasant manner.
                            You are interacting with customers through an online chat system.
                            You can analyze the content of your client's submission to determine the type of contractor you need through the license classification in the California Contractor State Licensing Board (CSLB).
                            Please speak Chinese.
                            Today's date is {current_date}.
                            Example 1:
                            If the output contains content in the form of a list or table, use the markdown format to output, requiring center alignment.
                            Example of centering:
                            | A | B | C |
                            | : -- -- -- -- -- -- : | : - - - - - - - - - - : | : - - - - - - - - - - : |
                            | 1 | 2 | 3 |
                            Example 2:
                            The user enters: "Address: 425 23rd Ave
                            Location: Surface, four area, 40 sqft in total
                            Job description: Apply 40 sqft stucco, including 2 windows l"
                            Output a Job description of the types of subcontractors required for the material involved. Use a list to show the types of subcontractors
                         """ + PromptTemplate.SYSTEM)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()), // Chat Memory
                        // logger
                        new SimpleLoggerAdvisor()
                )
                .defaultOptions(OpenAiChatOptions.builder().model(MODEL_NAME).temperature(0.0).build())
                .defaultTools(syncMcpToolCallbackProvider.getToolCallbacks())
                .build();
        // @formatter:on
    }

    public Flux<String> chat(String chatId, String userMessageContent) {

        return this.chatClient.prompt()
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .user(userMessageContent)
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .stream()
                .content();
    }

    public Flux<String> chat(String chatId, String userMessageContent, MultipartFile file) throws IOException {
        ByteArrayResource byteArrayResource = new ByteArrayResource(file.getBytes());
        UserMessage userMessage = new UserMessage(userMessageContent, new Media(MimeTypeUtils.IMAGE_PNG, byteArrayResource));
        return this.chatClient.prompt(new Prompt(userMessage))
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .stream()
                .content();
    }


    public String chatBlock(String chatId, String userMessageContent, MultipartFile file) throws IOException {
        ByteArrayResource byteArrayResource = new ByteArrayResource(file.getBytes());
        UserMessage userMessage = new UserMessage(userMessageContent, new Media(MimeTypeUtils.IMAGE_PNG, byteArrayResource));
        return this.chatClient.prompt(new Prompt(userMessage))
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call()
                .content();
    }


    public String chatBlock(String chatId, String userMessageContent) throws JsonProcessingException {

        return this.chatClient
                .prompt()
                .user(userMessageContent)
                .system(s -> s.param("current_date", LocalDate.now().toString())
                        .param("params",PromptBuilder.getAuthorization()).param("workspace_id",ContextUtils.getWorkspaceId()))
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call()
                .content();
    }
}
