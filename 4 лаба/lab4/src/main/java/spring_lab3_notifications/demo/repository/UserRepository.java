package spring_lab3_notifications.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring_lab3_notifications.demo.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
