package spring_lab3_notifications.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import spring_lab3_notifications.demo.model.enums.NotificationChannel;
import spring_lab3_notifications.demo.model.enums.NotificationStatus;

import java.time.LocalDateTime;

@Data
public class NotificationDto {

    private Long id;

    @NotNull(message = "Recipient ID is required")
    private Long recipientId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Channel is required")
    private NotificationChannel channel;

    private NotificationStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
}
