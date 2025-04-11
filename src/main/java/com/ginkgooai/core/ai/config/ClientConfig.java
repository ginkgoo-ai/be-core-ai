package com.ginkgooai.core.ai.config;

import com.ginkgooai.core.ai.dto.ClassifyRecord;
import com.ginkgooai.core.ai.dto.EvaluatorRecord;
import com.ginkgooai.core.ai.prompt.PromptTemplate;
import com.ginkgooai.core.ai.prompt.project.ProjectPrompt;
import com.ginkgooai.core.ai.prompt.email.EmailPrompt;
import com.ginkgooai.core.ai.prompt.workflow.WorkflowPrompt;
import io.modelcontextprotocol.client.McpSyncClient;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    @Value("${OPEN_AI_MODEL:}")
    private String MODEL_NAME;

    final VectorStore vectorStore;

    /**
     * Initializes and configures the primary chat client with default system messages and advisors
     * 
     * @param chatModel The OpenAI chat model to use
     * @param syncMcpToolCallbackProvider Provider for tool callbacks
     * @return Configured ChatClient instance for regular chat interactions
     */
    @Bean(name = "chatClient")
    @Primary
    public ChatClient initChatClient(OpenAiChatModel chatModel, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider) {
        return ChatClient.builder(chatModel)
                .defaultSystem(ProjectPrompt.SYSTEM)
                .defaultAdvisors(List.of(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()), // CHAT MEMORY
                        new SimpleLoggerAdvisor(),
                        new QuestionAnswerAdvisor(vectorStore)))
                .defaultTools(syncMcpToolCallbackProvider.getToolCallbacks()).build();

    }

    @Bean(name = "evaluatorClient")
    public ChatClient initEvaluatorClient(OpenAiChatModel chatModel, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider) {
        BeanOutputConverter<EvaluatorRecord> classifyRecordBeanOutputConverter = new BeanOutputConverter<>(EvaluatorRecord.class);

        return ChatClient.builder(chatModel)
                .defaultSystem(PromptTemplate.EVALUATOR)
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .defaultOptions(OpenAiChatOptions.builder().model(MODEL_NAME).temperature(0.0).responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, classifyRecordBeanOutputConverter.getJsonSchema())).build())
                .build();

    }

    @Bean(name = "commonClient")
    public ChatClient initCommonClient(OpenAiChatModel chatModel, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider) {


        return ChatClient.builder(chatModel)
                .defaultSystem(EmailPrompt.SYSTEM+ ProjectPrompt.SYSTEM_PROJECT)
                .defaultOptions(OpenAiChatOptions.builder().model(MODEL_NAME).temperature(0.0).build())
                .defaultAdvisors(List.of(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()), // CHAT MEMORY
                        new SimpleLoggerAdvisor(),
                        new QuestionAnswerAdvisor(vectorStore)
                ))
                .defaultTools(syncMcpToolCallbackProvider.getToolCallbacks()).build();
    }

    @Bean(name = "projectClient")
    @Primary
    public ChatClient initProjectClient(OpenAiChatModel chatModel, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider) {
        return ChatClient.builder(chatModel)
                .defaultSystem(ProjectPrompt.SYSTEM + ProjectPrompt.SYSTEM_PROJECT)
                .defaultAdvisors(List.of(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()), // CHAT MEMORY
                        new SimpleLoggerAdvisor(),
                        new QuestionAnswerAdvisor(vectorStore)))
                .defaultTools(syncMcpToolCallbackProvider.getToolCallbacks()).build();

    }


    @Bean(name = "workflowClient")
    public ChatClient initWorkFlowClient(OpenAiChatModel chatModel, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider) {
        return ChatClient.builder(chatModel)
                .defaultSystem(WorkflowPrompt.DEFAULT)
                .defaultOptions(OpenAiChatOptions.builder().model(MODEL_NAME).temperature(0.0).build())
                .defaultAdvisors(List.of(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()), // CHAT MEMORY
                        new SimpleLoggerAdvisor()))
                .defaultTools(syncMcpToolCallbackProvider.getToolCallbacks()).build();

    }

    /**
     * Initializes and configures a specialized chat client for email processing
     * 
     * @param chatModel The OpenAI chat model to use
     * @param syncMcpToolCallbackProvider Provider for tool callbacks
     * @return Configured ChatClient instance for email-specific interactions
     */
    @Bean(name = "emailClient")
    public ChatClient initEmailClient(OpenAiChatModel chatModel, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider) {
        return ChatClient.builder(chatModel)
                .defaultSystem(EmailPrompt.SYSTEM)
                .defaultOptions(OpenAiChatOptions.builder().model(MODEL_NAME).temperature(0.0).build())
                .defaultAdvisors(List.of(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()), // CHAT MEMORY
                        new SimpleLoggerAdvisor(),
                        new QuestionAnswerAdvisor(vectorStore)))
                .defaultTools(syncMcpToolCallbackProvider.getToolCallbacks()).build();
    }


    @Bean(name = "classificationAgent")
    ChatClient classificationAgent(ChatClient.Builder builder) {
        BeanOutputConverter<ClassifyRecord> classifyRecordBeanOutputConverter = new BeanOutputConverter<>(ClassifyRecord.class);

        return builder
                .defaultSystem(PromptTemplate.CLASSIFY)
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .defaultOptions(OpenAiChatOptions.builder().model(MODEL_NAME).temperature(0.0).responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, classifyRecordBeanOutputConverter.getJsonSchema())).build())
                .build();
    }



}
