package com.ginkgooai.core.ai.controller;

import com.ginkgooai.core.ai.prompt.PromptTemplate;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@RestController
@RequestMapping("/v2/chat")
public class ChatFluxController {

    @Resource
    private ChatClient client;

    @Value("${OPEN_AI_MODEL:}")
    private String MODEL_NAME;

    @Value("${CHAT_MEMORY_RETRIEVE_SIZE_KEY:")
    private String CHAT_MEMORY_RETRIEVE_SIZE_KEY;


    @GetMapping()
    Flux<String> generationFlux(@RequestParam String userInput, @RequestParam(required = false, defaultValue = "simple-chat") String chatId) {
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();

        userInput = userInput + String.format(PromptTemplate.TOKEN, attributes.getRequest()
                .getHeader(HttpHeaders.AUTHORIZATION));

        return client
                .prompt(
                        new Prompt(List.of(new UserMessage(userInput)),
                                OpenAiChatOptions.builder().model(MODEL_NAME).temperature(0.0).build()))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, CHAT_MEMORY_RETRIEVE_SIZE_KEY))
                .stream().content();

    }

}