package spring_lab3_notifications.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring_lab3_notifications.demo.model.entity.Notification;
import spring_lab3_notifications.demo.model.enums.NotificationChannel;
import spring_lab3_notifications.demo.model.enums.NotificationStatus;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStatusAndChannel(NotificationStatus status, NotificationChannel channel);

    List<Notification> findAllByOrderByCreatedAtDesc();

    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.status = :status")
    List<Notification> findByRecipientIdAndStatus(@Param("recipientId") Long recipientId, @Param("status") NotificationStatus status);
}
