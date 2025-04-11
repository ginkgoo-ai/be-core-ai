package com.ginkgooai.core.ai.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ginkgooai.core.ai.prompt.PromptBuilder;
import com.ginkgooai.core.ai.tools.VectorTools;
import com.ginkgooai.core.common.utils.ContextUtils;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;


@Component("WORKFLOW")
public class WorkflowAgent implements Agent{

    @Value("${OPEN_AI_MODEL:}")
    private String MODEL_NAME;

    @Resource(name = "workflowClient")
    private ChatClient workflowClient;

    @Override
    public String execute(String chatId, String input) throws JsonProcessingException {
        return workflowClient
                .prompt(
                        new Prompt(List.of(
                                new UserMessage(PromptBuilder.create(input, ContextUtils.getWorkspaceId())))))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10000))
                .tools(new VectorTools())
                .call().content();
    }

    @Override
    public Flux<String> executeAsync(String chatId, String input) throws JsonProcessingException {
        return workflowClient
                .prompt(
                        new Prompt(List.of(
                                new UserMessage(PromptBuilder.create(input, ContextUtils.getWorkspaceId())))))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10000))
                .tools(new VectorTools())
                .stream().content();
    }
}
