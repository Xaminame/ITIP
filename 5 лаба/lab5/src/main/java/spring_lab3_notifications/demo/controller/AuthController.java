package spring_lab3_notifications.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import spring_lab3_notifications.demo.model.dto.LoginRequest;
import spring_lab3_notifications.demo.model.dto.RegisterRequest;
import spring_lab3_notifications.demo.security.JwtService;
import spring_lab3_notifications.demo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }

    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin(@RequestBody @Valid RegisterRequest request) {
        authService.registerAdmin(request);
        return ResponseEntity.ok("Администратор успешно зарегистрирован");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Неверный логин или пароль");
        }

        String token = jwtService.generateToken(request.getEmail());
        return ResponseEntity.ok(token);
    }
}
