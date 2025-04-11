package com.ginkgooai.core.ai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ginkgooai.core.ai.agent.AgentCombination;
import com.ginkgooai.core.ai.converter.Converter;
import com.ginkgooai.core.ai.dto.ClassifyRecord;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/chat")
@Slf4j
public class ChatController {

    @Resource(name = "classificationAgent")
    private ChatClient classificationAgent;

    @Resource
    private AgentCombination agentCombination;

    @GetMapping
    String generation(
            @RequestHeader(required = false,value = "x-workspace-id") String workspaceId,
            @RequestParam String userInput,
            @RequestParam(required = false, defaultValue = "simple-chat") String chatId) throws JsonProcessingException {

        String content = classificationAgent.prompt().user(userInput).call().content();
        assert content != null;
        ClassifyRecord convert = Converter.getConverter(ClassifyRecord.class).convert(content);
        assert convert != null && convert.steps()!= null;
        log.info("convert step: {}", convert.steps().name());
        return agentCombination.execute(convert.steps().name(), userInput, chatId);


    }

}