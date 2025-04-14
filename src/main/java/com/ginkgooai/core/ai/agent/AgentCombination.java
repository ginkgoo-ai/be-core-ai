package com.ginkgooai.core.ai.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;


//@Component
@RequiredArgsConstructor
public class AgentCombination {

    final Map<String, Agent> agentMap;

    public Flux<String> executeFlux(String agentName, String userInput, String chatId) throws JsonProcessingException {
        return agentMap.get(agentName).executeAsync(chatId, userInput);
    }
    
    public String execute(String agentName, String userInput, String chatId) throws JsonProcessingException {
        return agentMap.get(agentName).execute(chatId, userInput);
    }
}
