package com.ginkgooai.core.ai.assistant;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.LocalDate;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
public class PictureVisionAssistant {

    @Value("${OPEN_AI_MODEL:}")
    private String MODEL_NAME;

    private final ChatClient chatClient;


    public PictureVisionAssistant(ChatClient.Builder modelBuilder, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider) {

        // @formatter:off
        this.chatClient = modelBuilder
                .defaultSystem("""
						您是“Jasper”加州装修总包公司的客户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。
						您正在通过在线聊天系统与客户互动。
						您可以通过加州承包人州执照委员会(CSLB)里的执业证分类，分析客户的发来的图片，分析出需要的承包商类型。
					   请讲中文。
					   今天的日期是 {current_date}.
					   如果输出的是包含列表或者表格形式的内容, 请使用markdown格式输出, 要求居中对齐。
					   居中对齐的示例:
					   | A | B | C |
					   |:------:|:----------:|:----------:|
					   | 1 | 2 | 3 |
					""")
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

    public Flux<String> chat(String chatId, String userMessageContent) {

        return this.chatClient.prompt()
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .user(userMessageContent)
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .stream()
                .content();
    }

    public Flux<String> chat(String chatId, String userMessageContent, MultipartFile file) throws IOException {
        ByteArrayResource byteArrayResource = new ByteArrayResource(file.getBytes());
        UserMessage userMessage = new UserMessage(userMessageContent, new Media(MimeTypeUtils.IMAGE_PNG, byteArrayResource));
        return this.chatClient.prompt(new Prompt(userMessage))
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .stream()
                .content();
    }


    public String chatBlock(String chatId, String userMessageContent, MultipartFile file) throws IOException {
        ByteArrayResource byteArrayResource = new ByteArrayResource(file.getBytes());
        UserMessage userMessage = new UserMessage(userMessageContent, new Media(MimeTypeUtils.IMAGE_PNG, byteArrayResource));
        return this.chatClient.prompt(new Prompt(userMessage))
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call()
                .content();
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
