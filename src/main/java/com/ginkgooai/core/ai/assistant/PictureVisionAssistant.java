package com.ginkgooai.core.ai.assistant;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.ginkgooai.core.ai.dto.QuickCommand;
import com.ginkgooai.core.ai.prompt.factory.PromptFactory;
import com.ginkgooai.core.ai.prompt.factory.PromptFactoryManager;
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
import org.springframework.util.CollectionUtils;
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

    private final String ERROR_MESSAGE = "We're experiencing technical difficulties. Please try again later or contact support at support@ginkgoo-support.com";

    private final String PROMPT = """	
                         # Role: Jasper Contractor Support Agent
                         You are the primary customer support agent for Jasper California Renovation Master Contractor.
                         
                         ## Communication Guidelines:
                         - Always maintain a professional, friendly and helpful tone
                         - Respond in clear, concise
                         - Use construction industry terminology appropriately
                         - Explain technical terms when needed
                         
                         ## Core Responsibilities:
                         1. Analyze project requirements to determine necessary CSLB license classifications
                         2. Provide accurate contractor recommendations based on:
                            - License type match
                            - Geographic proximity
                            - Availability
                            - Customer ratings
                         3. Clearly explain license classifications and requirements
                         4. Handle customer inquiries about:
                            - Licensing
                            - Project timelines
                            - Material requirements
                            - Cost estimates
                         5. When search for contractors ,if no distance is analyzed, set the default distance is 80467.2 meters
                         ## System Information:
                         - Current date: {current_date}
                         - Error message: {error_message}
                         - CSLB license database access: enabled
                         
                         {response_format}
                         {business_format}
                         """;
    public PictureVisionAssistant(ChatClient.Builder modelBuilder, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider) {

        // @formatter:off
        this.chatClient = modelBuilder
                .defaultSystem(PROMPT)
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
                .system(s -> s.param("current_date", LocalDate.now().toString())
                        .param("error_message",ERROR_MESSAGE)
                )
                .user(userMessageContent)
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .stream()
                .content();
    }

    public Flux<String> chat(String chatId, String userMessageContent, MultipartFile file, List<QuickCommand> types) throws IOException {
        PromptFactory factory = PromptFactoryManager.getFactory(types);
        if (file == null) {
            return this.chatClient.prompt()
                    .user(userMessageContent)
                    .system(s -> {
                                s.param("current_date", LocalDate.now().toString())
                                        .param("error_message", ERROR_MESSAGE)
                                        .param("response_format", factory.getResponseFormat())
                                        .param("business_format", factory.getBusinessFormat());
                            }
                    )
                    .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                    .stream()
                    .content();
        }
        ByteArrayResource byteArrayResource = new ByteArrayResource(file.getBytes());
        UserMessage userMessage = new UserMessage(userMessageContent, new Media(MimeTypeUtils.IMAGE_PNG, byteArrayResource));
        return this.chatClient.prompt(new Prompt(userMessage))
                .system(s -> {
                            s.param("current_date", LocalDate.now().toString())
                                    .param("error_message",ERROR_MESSAGE)
                                    .param("response_format", factory.getResponseFormat())
                                    .param("business_format", factory.getBusinessFormat());
                        }
                )
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .stream()
                .content();
    }


    public String chatBlock(String chatId, String userMessageContent, MultipartFile file) throws IOException {
        ByteArrayResource byteArrayResource = new ByteArrayResource(file.getBytes());
        UserMessage userMessage = new UserMessage(userMessageContent, new Media(MimeTypeUtils.IMAGE_PNG, byteArrayResource));
        return this.chatClient.prompt(new Prompt(userMessage))
                .system(s -> s.param("current_date", LocalDate.now().toString())
                        .param("error_message",ERROR_MESSAGE)

                )
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call()
                .content();
    }


    public String chatBlock(String chatId, String userMessageContent, List<QuickCommand> types) throws JsonProcessingException {
        PromptFactory factory = PromptFactoryManager.getFactory(types);

        return this.chatClient
                .prompt()
                .user(userMessageContent)
                .system(s -> {
                            s.param("current_date", LocalDate.now().toString())
                                    .param("error_message",ERROR_MESSAGE)
                                    .param("response_format", factory.getResponseFormat())
                                    .param("business_format", factory.getBusinessFormat());;

                        }
                )
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call()
                .content();

    }

}
