package com.ginkgooai.core.ai.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

@Configuration
public class RedissonConfig {

    /**
     * Creates and configures a Redisson client using Redis properties
     * 
     * @param redisProperties The Redis connection properties
     * @return Configured RedissonClient instance
     */
    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        String prefix = "redis://";
        Config config = new Config();
        config.useSingleServer()
                .setAddress(prefix + redisProperties.getHost() + ":" + redisProperties.getPort());
        if (!ObjectUtils.isEmpty(redisProperties.getPassword())) {
            config.useSingleServer().setPassword(redisProperties.getPassword());
        }
        return Redisson.create(config);
    }
}