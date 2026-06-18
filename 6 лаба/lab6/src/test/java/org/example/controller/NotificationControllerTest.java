package org.example.controller;
import org.example.model.Notification;
import org.example.service.NotificationService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
class NotificationControllerTest {
    @Test void shouldReturnNotification(){ NotificationService service = mock(NotificationService.class); when(service.getNotificationById(1L)).thenReturn(new Notification(1L, "web")); assertEquals("web", new NotificationController(service).one(1L).getMessage()); }
}