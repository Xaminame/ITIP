package spring_lab3_notifications.demo.service;

public interface MessageService {
    void sendMessage(String message, String recipient);
    String getServiceType();
}
