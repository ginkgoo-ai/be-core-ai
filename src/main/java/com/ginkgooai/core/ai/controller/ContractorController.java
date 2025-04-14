package com.ginkgooai.core.ai.controller;


import com.ginkgooai.core.ai.assistant.ContractorAssistant;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/assistant/contractors")
@RequiredArgsConstructor
public class ContractorController {

    private final ContractorAssistant contractorAssistant;

    @GetMapping("/block")
    String generationBlock(
            @RequestParam String userInput,
            @RequestParam(required = false, defaultValue = "simple-chat") String chatId) throws IOException {

        return contractorAssistant.chatBlock(chatId, userInput);

    }
}
