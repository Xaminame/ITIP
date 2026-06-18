package org.example.springlab3notifications.controller;

import org.example.springlab3notifications.service.NotificationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {
    private final NotificationManager notificationManager;

    public NotificationController(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @GetMapping("/notify")
    public String notify(@RequestParam String message, @RequestParam String email) {
        notificationManager.notify(message, email);
        return "Уведомление отправлено (аннотации)";
    }
}
