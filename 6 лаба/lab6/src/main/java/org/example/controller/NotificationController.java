package org.example.controller;
import org.example.model.Notification;
import org.example.service.NotificationService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService service;
    public NotificationController(NotificationService service){this.service=service;}
    @GetMapping("/{id}") public Notification one(@PathVariable Long id){return service.getNotificationById(id);}
}