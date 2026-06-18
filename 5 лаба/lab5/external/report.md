# Лабораторная работа №5

## Разработка веб-приложения на Spring Boot. Основы Spring Security

### Цель работы

Изучить основы обеспечения безопасности в Spring Boot-приложении: научиться настраивать аутентификацию и авторизацию пользователей, ограничивать доступ к ресурсам приложения, работать с ролями и механизмами защиты запросов, а также познакомиться с основными подходами к хранению состояния пользователя в защищённой системе.

---

### Ход выполнения работы

#### Часть 0. Подготовка

Использован проект `spring-lab3-notifications`, расширенный в лабораторной №4. Приложение работает с PostgreSQL, таблицы `users` и `notifications` уже созданы.

#### Часть 1. Подключение Spring Security

В `pom.xml` добавлены зависимости:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

Роль зависимостей:
- `spring-boot-starter-security` — подключает базовый механизм Spring Security
- `jjwt-api` — API для создания и чтения JWT
- `jjwt-impl` — внутренняя реализация работы с JWT
- `jjwt-jackson` — сериализация и десериализация JWT payload в JSON

#### Часть 2. Расширение модели пользователя

##### 2.1. Перечисление роли пользователя

Создан enum `UserRole`:

```java
package spring_lab3_notifications.demo.model.enums;

public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN
}
```

##### 2.2. Обновление сущности User

В сущность `User` добавлены поля `password` и `role`:

```java
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private String phone;
    private String deviceToken;
    private String telegramChatId;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

Теперь `email` используется как логин, `password` — как пароль, а `role` определяет права пользователя.

##### 2.3. Создание DTO

Создан `RegisterRequest` для регистрации:

```java
@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать не менее 6 символов")
    private String password;
}
```

Создан `LoginRequest` для логина:

```java
@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    private String password;
}
```

#### Часть 3. Подготовка репозитория и загрузки пользователя

##### 3.1. Обновление UserRepository

Добавлен метод поиска по email:

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

##### 3.2. Класс CustomUserDetails

Создан адаптер между сущностью `User` и интерфейсом `UserDetails`:

```java
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public String getUsername() { return user.getEmail(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public User getUser() { return user; }
}
```

Что делает этот класс:
- хранит ссылку на сущность `User`
- возвращает email как логин пользователя
- возвращает хэш пароля из базы данных
- преобразует роль в `GrantedAuthority`
- позволяет получить исходную сущность через `getUser()`

##### 3.3. Класс CustomUserDetailsService

```java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        return new CustomUserDetails(user);
    }
}
```

Spring Security передаёт в метод `loadUserByUsername()` логин пользователя, мы ищем его в базе по email и оборачиваем в `CustomUserDetails`.

#### Часть 4. Шифрование паролей и конфигурация безопасности

Создан класс `SecurityConfig`:

```java
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/users/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/notifications/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

Что делает конфигурация:
- **PasswordEncoder** — создаёт объект для шифрования паролей через BCrypt
- **DaoAuthenticationProvider** — загружает пользователя из базы через `CustomUserDetailsService` и сравнивает пароль через `PasswordEncoder`
- **AuthenticationManager** — используется при ручной аутентификации в `AuthController`
- **SecurityFilterChain** — задаёт правила безопасности:
  - `/auth/**` доступны всем
  - `/admin/**` доступны только администраторам
  - `/users/**` и `/notifications/**` доступны аутентифицированным пользователям с ролями USER и ADMIN
  - остальные URL требуют аутентификации
- `csrf.disable()` — отключаем CSRF для REST API
- `SessionCreationPolicy.STATELESS` — полностью stateless (JWT)
- JWT-фильтр добавляется в цепочку фильтров до `UsernamePasswordAuthenticationFilter`

#### Часть 5. Регистрация пользователя

##### 5.1. AuthService

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.ROLE_USER);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void registerAdmin(RegisterRequest request) {
        // аналогично, но с ролью ROLE_ADMIN
    }
}
```

Реализована проверка уникальности email при регистрации.

##### 5.2. AuthController

```java
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
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Неверный логин или пароль");
        }
        String token = jwtService.generateToken(request.getEmail());
        return ResponseEntity.ok(token);
    }
}
```

##### 5.3. Проверка регистрации

Запрос в Postman:
- `POST http://localhost:8080/auth/register`

```json
{
  "name": "Иван Иванов",
  "email": "ivan@example.com",
  "password": "qwerty123"
}
```

Результат:
- пользователь появился в таблице `users`
- пароль сохранён в виде хэша BCrypt
- роль установлена в `ROLE_USER`

#### Часть 6–7. Авторизация по ролям

В конфигурации задано разграничение доступа:
- `/admin/**` — только для ADMIN
- `/users/**` — для USER и ADMIN
- `/notifications/**` — для USER и ADMIN

Создан `AdminController`:

```java
@RestController
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/ping")
    public String ping() {
        return "Только для администратора";
    }
}
```

Если пользователь с ролью `ROLE_USER` попытается обратиться к `/admin/ping`, он получит ошибку 403.

Также добавлена методная авторизация на удаление пользователя:

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) { ... }
```

#### Часть 8. Фильтры Spring Security

Запрос проходит через цепочку фильтров:
1. Клиент отправляет HTTP-запрос
2. Запрос попадает в servlet-container
3. Контейнер передаёт его в `DelegatingFilterProxy`
4. `DelegatingFilterProxy` направляет запрос в `FilterChainProxy`
5. Далее последовательно срабатывают security-фильтры
6. Если аутентификация и авторизация успешны, запрос доходит до контроллера

Основные фильтры:
- `UsernamePasswordAuthenticationFilter` — обрабатывает логин через форму
- `BasicAuthenticationFilter` — обрабатывает HTTP Basic
- `SecurityContextPersistenceFilter` — сохраняет/загружает данные о пользователе из сессии
- `ExceptionTranslationFilter` — переводит ошибки безопасности в HTTP-ответы
- `FilterSecurityInterceptor` — выполняет финальную проверку доступа к URL

#### Часть 9–10. Сессии vs JWT

##### Сессионная аутентификация (stateful):
- Сервер хранит состояние
- Используется cookie `JSESSIONID`
- Хорошо подходит для браузерных приложений с формой логина

##### JWT (stateless):
- Сервер не хранит состояние
- Клиент сам носит токен в каждом запросе
- Хорошо подходит для REST API, мобильных приложений и микросервисов

##### JwtService

```java
@Service
public class JwtService {
    private final String secret = "verySecretKeyForJwtTokenVerySecretKey12345";

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
```

##### JwtAuthenticationFilter

Фильтр извлекает JWT из заголовка `Authorization: Bearer <token>`, проверяет его валидность, загружает пользователя и помещает `Authentication` в `SecurityContext`.

#### Часть 11–12. Проверка работы

1. **Регистрация**: `POST /auth/register` — создаёт пользователя с хэшированным паролем
2. **Логин (JWT)**: `POST /auth/login` — возвращает JWT-токен
3. **Доступ с токеном**: запросы к `/users/all`, `/notifications/all` с заголовком `Authorization: Bearer <token>` — доступ разрешён
4. **Проверка ролей**: `/admin/ping` — доступен только ADMIN, обычный USER получает 403
5. **Без токена**: запросы к защищённым URL возвращают 403

---

### Самостоятельные задания (выполненные)

1. **Endpoint регистрации администратора** — `POST /auth/register-admin`
2. **Проверка уникальности email** — при повторной регистрации с тем же email выбрасывается ошибка
3. **Обработка ошибок при неверном логине/пароле** — возвращается 401 с сообщением
4. **Метод extractUsername() в JwtService** — реализован, извлекает subject из токена
5. **Проверка срока действия токена** — метод `isTokenValid()` проверяет expiration
6. **SessionCreationPolicy.STATELESS** — приложение полностью stateless на JWT
7. **Защита метода удаления пользователя** — `@PreAuthorize("hasRole('ADMIN')")`

---

### Вывод

В ходе лабораторной работы приложение системы уведомлений было дополнено механизмами безопасности с использованием Spring Security. Реализованы: регистрация пользователей с хэшированием паролей через BCrypt, авторизация по ролям (USER/ADMIN), аутентификация через JWT-токены. Приложение работает в stateless-режиме, что делает его пригодным для REST API. Изучены ключевые компоненты Spring Security: SecurityFilterChain, UserDetailsService, PasswordEncoder, AuthenticationManager, JWT-фильтры.
