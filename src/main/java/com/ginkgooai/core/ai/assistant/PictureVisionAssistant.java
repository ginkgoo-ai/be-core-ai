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

    private final String CARD_JSON = """
                                          ```card
                                            [{
                                              "type": "card",
                                              "title": "Occupation Analysis",
                                              "content": {
                                                "key1": "value1",
                                                "key2": "value2"
                                              }
                                            }]
                                          ```
                                          """;

    private final String ERROR_MESSAGE = "System encountered a small problem, please try again later";

    private final String CONTRACTOR_PROMPT = """
                            ## Ability Setting 2:
                            If the user enters something similar to the following use a card to show the types of subcontractors
                            Output a Job description of the types of subcontractors required for the material involved.
                            Please use mcp tools to get the contractor list.
                          ###  Example :
                            The user enters: "Address: 425 23rd Ave
                                              Location: Surface, four area, 40 sqft in total
                                              Job description: Apply 40 sqft stucco, including 2 windows l"
                          """;


    final String PROMPT = """	
                         #Role
                         You are a customer chat support agent for "Jasper" California Renovation Master Contractor. Please reply in a friendly, helpful and pleasant manner.
                            You are interacting with customers through an online chat system.
                            You can analyze the content of your client's submission to determine the type of contractor you need through the license classification in the California Contractor State Licensing Board (CSLB).
                            Please speak English first.
                            Today's date is {current_date}.
                            When the system encounters problems, prompt user {error_message}.
                            Extract parameters strictly according to user input, and if the user input is not complete, you should ask the user to complete the information.
                            Please use the California Distributor License Classification for the types of licenses involved.
                          ##  Ability Setting 1:
                            If the output contains content in the form of a list or table or card, use the json format to output. It always starts with ```card and ends with ```.
                          ###  Example :
                             {card_json}
                             type description: fix type "card",
                             title description: the description of the card,
                             content description: json format
                         
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
                        .param("card_json",CARD_JSON)
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
                    .system(s -> s.param("current_date", LocalDate.now().toString())
                            .param("card_json",CARD_JSON)
                            .param("error_message",ERROR_MESSAGE)

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
                                            .param("card_json",CARD_JSON)
                                            .param("error_message",ERROR_MESSAGE);
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
                        .param("card_json",CARD_JSON)
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
                                        .param("card_json",CARD_JSON)
                                        .param("error_message",ERROR_MESSAGE);

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
