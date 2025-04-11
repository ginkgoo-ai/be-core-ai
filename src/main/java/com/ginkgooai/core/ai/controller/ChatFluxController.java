package com.ginkgooai.core.ai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ginkgooai.core.ai.agent.AgentCombination;
import com.ginkgooai.core.ai.converter.Converter;
import com.ginkgooai.core.ai.dto.ClassifyRecord;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v2/chat")
public class ChatFluxController {

    @Resource(name = "classificationAgent")
    private ChatClient classificationAgent;

    @Resource
    private AgentCombination agentCombination;

    /**
     * Streams chat responses based on user input using reactive approach
     * 
     * @param userInput The input text from the user
     * @param chatId Unique identifier for the chat session (defaults to "simple-chat")
     * @return Flux stream of generated response content
     */
    @GetMapping(produces = "text/event-stream; charset=utf-8")
    Flux<String> generationFlux(
            @RequestHeader(required = false,value = "x-workspace-id") String workspaceId,
            @RequestParam String userInput,
            @RequestParam(required = false, defaultValue = "simple-chat") String chatId) throws JsonProcessingException {

        String content = classificationAgent.prompt().user(userInput).call().content();
        assert content != null;
        ClassifyRecord convert = Converter.getConverter(ClassifyRecord.class).convert(content);
        assert convert != null && convert.steps()!= null;
        return agentCombination.executeFlux(convert.steps().name(), userInput, chatId);

    }

}