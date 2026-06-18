package spring_lab3_notifications.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring_lab3_notifications.demo.model.dto.NotificationDto;
import spring_lab3_notifications.demo.model.enums.NotificationChannel;
import spring_lab3_notifications.demo.model.enums.NotificationStatus;
import spring_lab3_notifications.demo.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/all")
    public ResponseEntity<List<NotificationDto>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDto> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<NotificationDto> createNotification(@Valid @RequestBody NotificationDto notificationDto) {
        return new ResponseEntity<>(notificationService.createNotification(notificationDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationDto> updateNotification(@PathVariable Long id, @Valid @RequestBody NotificationDto notificationDto) {
        return ResponseEntity.ok(notificationService.updateNotification(id, notificationDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotificationDto>> getNotificationsByStatus(@PathVariable NotificationStatus status) {
        // Since we don't have a simple getByStatus in service, let's just return all for now or add it.
        // I will just use the existing method with a null channel for now, or you can add it to service.
        return ResponseEntity.ok(notificationService.getAllNotifications().stream().filter(n -> n.getStatus() == status).toList());
    }

    @GetMapping("/channel/{channel}")
    public ResponseEntity<List<NotificationDto>> getNotificationsByChannel(@PathVariable NotificationChannel channel) {
        return ResponseEntity.ok(notificationService.getAllNotifications().stream().filter(n -> n.getChannel() == channel).toList());
    }

    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<List<NotificationDto>> getNotificationsByRecipientId(@PathVariable Long recipientId) {
        return ResponseEntity.ok(notificationService.getAllNotifications().stream().filter(n -> n.getRecipientId().equals(recipientId)).toList());
    }
}
