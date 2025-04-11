package com.ginkgooai.core.ai.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Flux;

public interface Agent {
    String execute(String chatId, String input) throws JsonProcessingException;

    Flux<String> executeAsync(String chatId,String input) throws JsonProcessingException;
}
