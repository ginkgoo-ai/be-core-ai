package com.ginkgooai.core.ai.config;

import com.ginkgooai.core.ai.prompt.PromptTemplate;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ClientConfig {

    @Bean
    public ChatClient init(OpenAiChatModel chatModel,SyncMcpToolCallbackProvider syncMcpToolCallbackProvider){

        return ChatClient.builder(chatModel)
                .defaultSystem(PromptTemplate.SYSTEM)
                .defaultAdvisors(List.of(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()), // CHAT MEMORY
                        new SimpleLoggerAdvisor()))
                .defaultTools(syncMcpToolCallbackProvider.getToolCallbacks()).build();
    }
}
