package org.example.service;
import org.example.model.Notification;
import org.example.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class NotificationServiceTest {
    @Test void shouldGetNotificationById(){ NotificationRepository repo = mock(NotificationRepository.class); when(repo.findById(1L)).thenReturn(Optional.of(new Notification(1L, "hello"))); assertEquals("hello", new NotificationService(repo).getNotificationById(1L).getMessage()); }
    @Test void shouldThrowWhenNotificationNotFound(){ NotificationRepository repo = mock(NotificationRepository.class); when(repo.findById(1L)).thenReturn(Optional.empty()); assertThrows(IllegalArgumentException.class, () -> new NotificationService(repo).getNotificationById(1L)); }
}