package com.ginkgooai.core.ai.config;

import com.ginkgooai.core.ai.prompt.PromptTemplate;
import com.ginkgooai.core.ai.prompt.chat.ChatPrompt;
import com.ginkgooai.core.ai.prompt.email.EmailPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
public class ClientConfig {

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
                .defaultSystem(ChatPrompt.SYSTEM + ChatPrompt.SYSTEM_PROJECT)
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
                .defaultAdvisors(List.of(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()), // CHAT MEMORY
                        new SimpleLoggerAdvisor()))
                .defaultTools(syncMcpToolCallbackProvider.getToolCallbacks()).build();
    }
}
