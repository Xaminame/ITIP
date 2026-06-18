package org.example.repository;
import org.example.model.User;
import java.util.Optional;
public interface UserRepository { Optional<User> findById(Long id); void delete(User user); }