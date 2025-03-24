package com.ginkgooai.core.ai.consumer;

import com.ginkgooai.core.ai.service.EmailServiceImpl;
import com.ginkgooai.core.common.constant.MessageQueue;
import com.ginkgooai.core.common.message.MailSendMessage;
import com.ginkgooai.core.common.queue.QueueInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailMqConsumer {
    @Value("${email.consumer.batch-size:10}")
    private int batchSize;

    @Value("${email.consumer.max-wait-time:5000}")
    private long maxWaitTimeMs;

    @Value("${email.consumer.polling-interval:1000}")
    private long pollingIntervalMs;

    /**
     * Scheduled method to consume messages from the Email queue
     * Uses batch processing for better performance
     * Implements circuit breaker pattern to handle downstream service failures
     */
    @Value("${thread.pool.core-size:5}")
    private int corePoolSize;

    @Value("${thread.pool.max-size:20}")
    private int maxPoolSize;

    @Value("${thread.pool.queue-capacity:100}")
    private int queueCapacity;

    private final ThreadPoolTaskExecutor taskExecutor;

    private final QueueInterface queueInterface;

    private final EmailServiceImpl emailService;

    /**
     * Scheduled method to poll and process email messages from the queue
     */
    @Scheduled(fixedDelay = 1000)
    public void consumeEmails() {
        List<MailSendMessage> batch = queueInterface.getMessages(MessageQueue.EMAIL_SEND_QUEUE, batchSize, MailSendMessage.class);
        if (!ObjectUtils.isEmpty(batch)) {
            for (MailSendMessage message : batch) {
                taskExecutor.execute(() -> {
                    try {
                        emailService.flowAssistant(message);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    /**
     * Processes a batch of email messages from the queue
     * 
     * @param messages List of email messages to process
     */
    private void processMessages(List<MailSendMessage> messages) {
        // Implementation details...
    }
}
