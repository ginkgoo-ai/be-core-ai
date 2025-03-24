package com.ginkgooai.core.ai.controller;


import com.ginkgooai.core.ai.service.EmailServiceImpl;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/v1/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailServiceImpl emailService;

    /**
     * Generates email content based on the provided email message
     * 
     * @param emailMessage The email message to process
     * @param emailTrace Trace identifier for the email processing (defaults to "simple-email-trace")
     * @return Generated email content
     * @throws IOException If an I/O error occurs
     * @throws MessagingException If an error occurs with the email message
     */
    @GetMapping
    public String generation(@RequestParam Message emailMessage, @RequestParam(required = false, defaultValue = "simple-email-trace") String emailTrace) throws IOException, MessagingException {

        return emailService.generation(emailMessage, emailTrace);

    }
}
