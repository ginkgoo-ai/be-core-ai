package com.ginkgooai.core.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BeCoreAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeCoreAiApplication.class, args);
    }

}
