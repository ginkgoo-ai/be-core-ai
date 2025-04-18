package com.ginkgooai.core.ai.assistant;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.ginkgooai.core.ai.dto.QuickCommand;
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

    private final String RESPONSE_FORMAT = """
        ## Response Format Requirements
        - Output must be in JSON format enclosed in ```card``` markers
        - Each contractor card must contain:
          * type: "card" (fixed value)
          * title: contractor's business name
          * content: detailed contractor information
        - Content must include these mandatory fields:
          * businessName: Legal business name
          * licenseNumber: CSLB license number (format: 8 digits)
          * address: Full business address
          * city: city
          * state: state
          * zip: zip code
          * phoneNumber: Contact number (format: (XXX) XXX-XXXX)
          * classification: License classification (e.g. B, C-10)
        
        Example Output:
        ```card
        [{
            "type": "card",
            "title": "SMITH ADRIAN CONSTRUCTION",
            "content": {
                "businessName": "SMITH ADRIAN CONSTRUCTION",
                "licenseNumber": "1028721",
                "address": "2460 HOWARD AVE, SAN FRANCISCO, CA 94116",
                "city": "SMITH ADRIAN CONSTRUCTION",
                "state": "CA",
                "zip": "94116",
                "phoneNumber": "(650) 400-5365",
                "classification": "B"
            }
        }]
        ```
        """;
    private final String CONTRACTOR_PROMPT = """
                         
                          ## Contractor Matching Instructions:
                          1. Analyze the project description to determine required license classifications
                          2. Prioritize contractors by:
                             - License match (primary)
                             - Distance from project location (secondary)
                             - Availability date (tertiary)
                             - Customer rating (quaternary)
                          3. Include detailed job description matching in response
                          4. Always verify contractor license status with CSLB database
                          5. Provide 3-5 best matching contractors
                         """;


    final String PROMPT = """	
                         # Role: Jasper Contractor Support Agent
                         You are the primary customer support agent for Jasper California Renovation Master Contractor.
                         
                         ## Communication Guidelines:
                         - Always maintain a professional, friendly and helpful tone
                         - Respond in clear, concise English
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
                         
                         ## System Information:
                         - Current date: {current_date}
                         - Error message: {error_message}
                         - CSLB license database access: enabled
                         
                         {response_format}
                         {contractor_prompt}
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
        if (file == null) {
            return this.chatClient.prompt()
                    .user(userMessageContent)
                    .system(s -> {
                                s.param("current_date", LocalDate.now().toString())
                                        .param("error_message", ERROR_MESSAGE)
                                        .param("response_format", RESPONSE_FORMAT);
                                if (!CollectionUtils.isEmpty(types) && types.contains(QuickCommand.CONTRACTORS_INFO)) {
                                    s.param("contractor_prompt", CONTRACTOR_PROMPT);
                                } else {
                                    s.param("contractor_prompt", "");
                                }
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
                                    .param("response_format", RESPONSE_FORMAT);
                            if (!CollectionUtils.isEmpty(types) && types.contains(QuickCommand.CONTRACTORS_INFO)) {
                                s.param("contractor_prompt", CONTRACTOR_PROMPT);
                            }else {
                                s.param("contractor_prompt", "");
                            }
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

        return this.chatClient
                .prompt()
                .user(userMessageContent)
                .system(s -> {
                            s.param("current_date", LocalDate.now().toString())
                                    .param("error_message",ERROR_MESSAGE)
                                    .param("response_format", RESPONSE_FORMAT);

                            if (!CollectionUtils.isEmpty(types) && types.contains(QuickCommand.CONTRACTORS_INFO)) {
                                s.param("contractor_prompt", CONTRACTOR_PROMPT);
                            }else {
                                s.param("contractor_prompt", "");
                            }
                        }
                )
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call()
                .content();

    }
}
