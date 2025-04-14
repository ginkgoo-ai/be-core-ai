package com.ginkgooai.core.ai.assistant;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
public class ContractorAssistant {

    @Value("${OPEN_AI_MODEL:}")
    private String MODEL_NAME;

    private final ChatClient chatClient;


    public ContractorAssistant(ChatClient.Builder modelBuilder,  SyncMcpToolCallbackProvider syncMcpToolCallbackProvider) {

        // @formatter:off
        this.chatClient = modelBuilder
                .defaultSystem("""
						您是“Jasper”加州装修总包公司的客户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。
						您正在通过在线聊天系统与客户互动。
						您可以通过分析用户发来的信息
						例如:
						Address: 425 23rd Ave
						Location: Surface, four area, 40 sqft in total
						Job description: Apply 40 sqft stucco, including 2 windows l
						分析出需要的加州承包人州执照委员会(CSLB)里的执业证分类，然后按距离分析出对应的承包商。
					   请讲中文。
					   今天的日期是 {current_date}.
					""")
				.defaultSystem("如果输出的是包含列表形式的内容，请使用markdown格式输出，在输出的开头加上 ```markdown ，在结尾加上 ``` 。")
				.defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()), // Chat Memory
                        // logger
                        new SimpleLoggerAdvisor()
                )
                .defaultOptions(OpenAiChatOptions.builder().model(MODEL_NAME).temperature(0.0).build())
				.defaultTools(syncMcpToolCallbackProvider.getToolCallbacks())
				.build();
        // @formatter:on
    }

    public String chatBlock(String chatId, String userMessageContent) {

        return this.chatClient.prompt()
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .user(userMessageContent)
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call()
                .content();
    }
}
