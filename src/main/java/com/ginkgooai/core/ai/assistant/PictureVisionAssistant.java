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
                         Please think step by step to provide the best possible service to our customers.
                         
                         ## Thought Process:
                         1. Analyze the user's input to understand their needs
                         2. Identify required CSLB license classifications
                            2.1 Analysis Protocol License Identification
                               - Phase 1: Scan for ALL C-Class codes (e.g., C-10, C-35)
                               - Phase 2: B-General License **ONLY** if:
                                 ✓ Zero C-Class matches found
                         3. Repeat the analysis of the classifications involved in the description
                         4. Consider geographic proximity and other matching criteria
                         5. Formulate appropriate response
                         
                         
                         ## Action Steps:
                         1. Always output full license classifications in exact format
                         2. Provide 3-5 best matching contractors with complete details
                         3. Include detailed job description matching in response
                         
                         ## Communication Guidelines:
                         - Maintain professional, friendly tone
                         - Use construction industry terminology appropriately
                         - Explain technical terms when needed
                         
                         ## System Information:
                         - Current date: {current_date}
                         - Error message: {error_message}
                         
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
