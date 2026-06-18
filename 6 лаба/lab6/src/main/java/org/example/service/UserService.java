package org.example.service;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
@Service
public class UserService {
    private final UserRepository repository;
    public UserService(UserRepository repository) { this.repository = repository; }
    public User getUserById(Long id) { return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found")); }
    public void deleteUser(Long id) { repository.delete(getUserById(id)); }
}