package org.example.springlab3notifications.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements MessageService {
    @Override
    public void sendMessage(String message, String recipient) {
        System.out.println("EMAIL to " + recipient + ": " + message);
    }
}
