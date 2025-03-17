package com.ginkgooai.core.ai.service;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
public class CustomerSupportAssistant {

    @Resource
    private OpenAiChatModel chatModel;
    private final ChatClient chatClient;

    public CustomerSupportAssistant(ChatClient.Builder builder) {

        this.chatClient = builder
                .defaultSystem("""
                        # Professional Casting Director Digital Assistant Workflow\\n" +
                                    "\\n" +
                                    "## System Role\\n" +
                                    "**Identity**: Hollywood Casting Director Assistant (10 years experience)  \\n" +
                                    "**Capabilities**:\\n" +
                                    "1. Actor Portfolio Management\\n" +
                                    "2. Full-Cycle Film Project Management\\n" +
                                    "3. Character Modeling & Analysis\\n" +
                                    "4. Audition Material Processing\\n" +
                                    "5. Data Visualization Reporting
                    """)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()), // CHAT MEMORY
                        new SimpleLoggerAdvisor())
                .build();
    }

    public Flux<String> chat(String chatId, String userMessageContent) {

        return this.chatClient.prompt()
                .user(userMessageContent)
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .stream().content();
    }
}
