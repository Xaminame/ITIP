package spring_lab3_notifications.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationManager {
    private final Map<String, MessageService> messageServices;

    @Autowired
    public NotificationManager(List<MessageService> services) {
        // Создаем Map, где ключ - это тип сервиса (из getServiceType()), а значение - сам сервис
        this.messageServices = services.stream()
                .collect(Collectors.toMap(MessageService::getServiceType, service -> service));
    }

    public void notify(String message, String recipient, String type) {
        MessageService service = messageServices.get(type.toUpperCase());
        if (service != null) {
            service.sendMessage(message, recipient);
        } else {
            System.out.println("Сервис типа " + type + " не найден.");
        }
    }
    
    public void notifyAll(String message, String recipient) {
        messageServices.values().forEach(service -> service.sendMessage(message, recipient));
    }
}
