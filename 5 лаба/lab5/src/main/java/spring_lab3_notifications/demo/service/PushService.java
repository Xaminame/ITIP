package spring_lab3_notifications.demo.service;

import org.springframework.stereotype.Service;

@Service
public class PushService implements MessageService {
    @Override
    public void sendMessage(String message, String recipient) {
        System.out.println("PUSH to " + recipient + ": " + message);
    }

    @Override
    public String getServiceType() {
        return "PUSH";
    }
}
