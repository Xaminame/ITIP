package org.example.repository;
import org.example.model.Notification;
import java.util.Optional;
public interface NotificationRepository { Optional<Notification> findById(Long id); }