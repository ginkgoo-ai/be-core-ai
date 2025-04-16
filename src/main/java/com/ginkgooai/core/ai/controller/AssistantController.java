package com.ginkgooai.core.ai.controller;

import com.ginkgooai.core.ai.assistant.PictureVisionAssistant;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private final PictureVisionAssistant pictureVisionAssistant;

    @PostMapping(produces = "text/event-stream; charset=utf-8")
    Flux<String> generationFlux(
            @RequestPart String message,
            @RequestParam(required = false, defaultValue = "simple-chat") String chatId) {

        return pictureVisionAssistant.chat(chatId, message);

    }

    @PostMapping(produces = "text/event-stream; charset=utf-8")
    Flux<String> generationFlux(
            @RequestPart String message,
            @RequestPart(required = false) MultipartFile file,
            @RequestParam(required = false, defaultValue = "simple-chat") String chatId) throws IOException {

        return pictureVisionAssistant.chat(chatId, message,file);

    }

    @PostMapping("/block")
    String fileBlock(
            @RequestPart String message,
            @RequestPart(required = false) MultipartFile file,
            @RequestParam(required = false, defaultValue = "simple-chat") String chatId) throws IOException {

        return pictureVisionAssistant.chatBlock(chatId, message,file);

    }

    @PostMapping("/block")
    String generationBlock(
            @RequestPart String message,
            @RequestParam(required = false, defaultValue = "simple-chat") String chatId) throws IOException {

        return pictureVisionAssistant.chatBlock(chatId, message);

    }
}
