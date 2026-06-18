package org.example.service;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class UserServiceTest {
    @Test void shouldGetUserById(){ UserRepository repo = mock(UserRepository.class); when(repo.findById(1L)).thenReturn(Optional.of(new User(1L, "a@b.ru"))); assertEquals("a@b.ru", new UserService(repo).getUserById(1L).getEmail()); }
    @Test void shouldDeleteUser(){ UserRepository repo = mock(UserRepository.class); User user = new User(1L, "a@b.ru"); when(repo.findById(1L)).thenReturn(Optional.of(user)); new UserService(repo).deleteUser(1L); verify(repo, times(1)).delete(user); }
}