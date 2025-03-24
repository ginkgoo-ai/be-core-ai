package com.ginkgooai.core.ai.config.mq;

import com.ginkgooai.core.common.queue.QueueInterface;
import com.ginkgooai.core.common.queue.QueueMessage;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedissonMQ implements QueueInterface {

    private final RedissonClient redissonClient;

    /**
     * Sends a message to the specified queue
     * 
     * @param queueName Name of the queue to send the message to
     * @param message The message to be sent
     * @param <T> Type of the queue message
     */
    @Override
    public <T extends QueueMessage> void send(String queueName, T message) {
        RQueue<T> queue = redissonClient.getQueue(queueName);
        message.setMsgId(UUID.randomUUID().toString());
        message.setTimestamp(System.currentTimeMillis());
        queue.offer(message);
    }

    /**
     * Subscribes to a queue with the specified listener
     * 
     * @param queueName Name of the queue to subscribe to
     * @param listener The message listener
     */
    @Override
    public void subscribe(String queueName, MessageListener listener) {
    }

    /**
     * Shuts down the Redisson client
     */
    @Override
    public void shutdown() {
        redissonClient.shutdown();
    }

    /**
     * Retrieves messages from the specified queue
     * 
     * @param queueName Name of the queue to get messages from
     * @param batchSize Number of messages to retrieve
     * @param clazz Class type of the messages
     * @param <T> Type of the queue message
     * @return List of retrieved messages
     */
    @Override
    public <T extends QueueMessage> List<T> getMessages(String queueName, int batchSize, Class<T> clazz) {
        RQueue<T> queue = redissonClient.getQueue(queueName);
        return queue.poll(batchSize);
    }

}
