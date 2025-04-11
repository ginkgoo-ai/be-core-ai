package com.ginkgooai.core.ai.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ginkgooai.core.ai.converter.Converter;
import com.ginkgooai.core.ai.dto.EvaluatorRecord;
import com.ginkgooai.core.ai.prompt.PromptBuilder;
import com.ginkgooai.core.ai.tools.VectorTools;
import com.ginkgooai.core.common.utils.ContextUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.ginkgooai.core.ai.dto.EvaluatorRecord.ScoreType.LOW;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component("EXECUTION")
@Slf4j
public class ExecutionAgent implements Agent{

    @Value("${OPEN_AI_MODEL:}")
    private String MODEL_NAME;

    @Resource
    private ChatClient chatClient;

    @Resource(name = "evaluatorClient")
    private ChatClient evaluatorClient;

    private static final BeanOutputConverter<EvaluatorRecord> EVALUATOR_CONVERTER =
            Converter.getConverter(EvaluatorRecord.class);
    @Override
    public String execute(String chatId, String input) throws JsonProcessingException {

        return chatClient
                .prompt(
                        new Prompt(List.of(
                                new UserMessage(PromptBuilder.create(input, ContextUtils.getWorkspaceId())))))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10000))
                .tools(new VectorTools())
                .call().content();
    }
//
//    @Override
//    public Flux<String> executeAsync(String chatId, String input) throws JsonProcessingException {
//
//        String content = chatClient
//                .prompt(
//                        new Prompt(List.of(
//                                new UserMessage(PromptBuilder.create(input, ContextUtils.getWorkspaceId())))))
//                .advisors(a -> a
//                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
//                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10000))
//                .tools(new VectorTools())
//                .call().content();
//
//        return evaluatorClient
//                .prompt(
//                        new Prompt(List.of(
//                                new UserMessage(PromptBuilder.create(content, ContextUtils.getWorkspaceId())))))
//                .advisors(a -> a
//                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
//                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10000))
//                .tools(new VectorTools())
//                .stream().content();
//    }



//    @Override
//    public String execute(String chatId, String input) throws JsonProcessingException {
//        final int MAX_RETRIES = 3;
//        String content = generateContent(chatId, input);
//        EvaluatorRecord record = evaluateContent(content);
//        log.info("evaluator record: {}", record);
//        for (int retryCount = 0;
//             record.score() == LOW && retryCount < MAX_RETRIES;
//             retryCount++) {
//            log.warn("Low score detected, retry attempt {}", retryCount+1);
//            content = generateContent(chatId, input);
//            record = evaluateContent(content);
//        }
//
//        if (record.score() == LOW) {
//            throw new RuntimeException("Failed to meet quality standards after retries");
//        }
//        return content;
//    }
//
//    private String generateContent(String chatId, String input) throws JsonProcessingException {
//        return chatClient.prompt(
//                new Prompt(List.of(
//                        new UserMessage(PromptBuilder.create(input, ContextUtils.getWorkspaceId())))))
//                .advisors(a -> a
//                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
//                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10000)).call().content();
//    }
//
//    private EvaluatorRecord evaluateContent(String content) {
//        String evaluator = evaluatorClient.prompt().user(content).call().content();
//        EvaluatorRecord record = EVALUATOR_CONVERTER.convert(evaluator);
//        if (record == null || record.score() == null) {
//            throw new RuntimeException("Invalid evaluation result");
//        }
//        return record;
//    }

    @Override
    public Flux<String> executeAsync(String chatId, String input) throws JsonProcessingException {

        String content = chatClient
                .prompt(
                        new Prompt(List.of(
                                new UserMessage(PromptBuilder.create(input, ContextUtils.getWorkspaceId())))))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10000))
                .tools(new VectorTools())
                .call().content();

        return evaluatorClient
                .prompt(
                        new Prompt(List.of(
                                new UserMessage(PromptBuilder.create(content, ContextUtils.getWorkspaceId())))))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10000))
                .tools(new VectorTools())
                .stream().content();
    }
}
