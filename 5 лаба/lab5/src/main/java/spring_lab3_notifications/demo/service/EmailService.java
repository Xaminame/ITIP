package spring_lab3_notifications.demo.service;

import org.springframework.stereotype.Service;

@Service("customEmail")
public class EmailService implements MessageService {
    @Override
    public void sendMessage(String message, String recipient) {
        System.out.println("EMAIL to " + recipient + ": " + message);
    }

    @Override
    public String getServiceType() {
        return "EMAIL";
    }
}
