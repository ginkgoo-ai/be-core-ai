package com.ginkgooai.core.ai.service;


import com.ginkgooai.core.ai.client.identity.IdentityClient;
import com.ginkgooai.core.ai.prompt.PromptBuilder;
import com.ginkgooai.core.common.message.MailSendMessage;
import jakarta.annotation.Resource;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@Service
public class EmailServiceImpl {

    @Resource
    private IdentityClient identityClient;

    @Resource
    @Qualifier("chatClient")
    private ChatClient client;

    @Value("${OPEN_AI_MODEL:}")
    private String MODEL_NAME;

    @Value("${CHAT_MEMORY_RETRIEVE_SIZE_KEY:")
    private String CHAT_MEMORY_RETRIEVE_SIZE_KEY;

    /**
     * Generates email content based on the provided email message
     * 
     * @param emailMessage The email message to process
     * @param emailTrace Trace identifier for the email processing
     * @return Generated email content
     * @throws IOException If an I/O error occurs
     * @throws MessagingException If an error occurs with the email message
     */
    public String generation(Message emailMessage, String emailTrace) throws IOException, MessagingException {

        return client
                .prompt(
                        new Prompt(List.of(new UserMessage(PromptBuilder.create((String)emailMessage.getContent()))),
                                OpenAiChatOptions.builder().model(MODEL_NAME).temperature(0.0).build()))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, emailTrace)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, CHAT_MEMORY_RETRIEVE_SIZE_KEY))
                .call().content();
    }

    public void flowAssistant(MailSendMessage emailMessage) throws IOException {

        String emailTrace = "";
        //todo emailTrace
        String content = client
                .prompt(
                        new Prompt(List.of(new UserMessage(PromptBuilder.create(emailMessage.toString()))),
                                OpenAiChatOptions.builder().model(MODEL_NAME).temperature(0.0).build()))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, emailTrace)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, CHAT_MEMORY_RETRIEVE_SIZE_KEY))
                .call().content();
    }
}
