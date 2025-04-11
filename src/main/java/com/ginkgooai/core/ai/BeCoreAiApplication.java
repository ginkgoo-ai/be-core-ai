package com.ginkgooai.core.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BeCoreAiApplication {

    public static void main(String[] args) {
        String proxyHost = "127.0.0.1";
        int port = 7897;
        System.setProperty("proxyHost", proxyHost);
        System.setProperty("proxyPort", Integer.toString(port));
        System.setProperty("proxyType", "4");
        System.setProperty("proxySet", "true");
        SpringApplication.run(BeCoreAiApplication.class, args);
    }

}
