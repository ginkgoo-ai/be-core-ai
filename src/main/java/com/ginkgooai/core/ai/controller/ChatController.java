package com.ginkgooai.core.ai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ginkgooai.core.ai.prompt.PromptBuilder;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@RestController
@RequestMapping("/v1/chat")
public class ChatController {

    @Resource
    private ChatClient client;

    @Value("${OPEN_AI_MODEL:}")
    private String MODEL_NAME;

    @Value("${CHAT_MEMORY_RETRIEVE_SIZE_KEY:")
    private String CHAT_MEMORY_RETRIEVE_SIZE_KEY;

    @GetMapping
    String generation(
                      @RequestHeader(required = false) String workspaceId,
                      @RequestParam String userInput,
                      @RequestParam(required = false, defaultValue = "simple-chat") String chatId) throws JsonProcessingException {

        return client
                .prompt(
                        new Prompt(List.of(new UserMessage(PromptBuilder.create(userInput,workspaceId))),
                        OpenAiChatOptions.builder().model(MODEL_NAME).temperature(0.0).build()))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, CHAT_MEMORY_RETRIEVE_SIZE_KEY))
                .call().content();

    }

}