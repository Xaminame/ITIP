package org.example.service;
import org.example.model.Notification;
import org.example.repository.NotificationRepository;
import org.springframework.stereotype.Service;
@Service
public class NotificationService {
    private final NotificationRepository repository;
    public NotificationService(NotificationRepository repository) { this.repository = repository; }
    public Notification getNotificationById(Long id) { return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Notification not found")); }
}