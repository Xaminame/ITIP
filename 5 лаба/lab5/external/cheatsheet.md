# Шпаргалка к лабораторной работе №5 — Spring Security

## Контрольные вопросы и ответы

### 1. Что такое аутентификация и чем она отличается от авторизации?

**Аутентификация** — проверка личности пользователя. Система отвечает на вопрос: «Кто выполняет запрос?» (проверка логина/пароля).

**Авторизация** — проверка прав доступа. Система отвечает на вопрос: «Что именно этому пользователю разрешено делать?» (проверка ролей и разрешений).

Аутентификация всегда идёт первой — нельзя проверить права, не зная, кто перед нами.

---

### 2. Что делает SecurityFilterChain?

`SecurityFilterChain` описывает правила безопасности и набор фильтров, которые применяются к запросам. В нём задаются:
- какие URL открыты (`permitAll()`)
- какие требуют определённой роли (`hasRole("ADMIN")`)
- какие требуют аутентификации (`authenticated()`)
- политика управления сессиями
- отключение CSRF
- подключение кастомных фильтров (например, JWT-фильтра)

---

### 3. Зачем нужен UserDetailsService?

`UserDetailsService` — интерфейс Spring Security для загрузки пользователя из базы данных. Он содержит один метод — `loadUserByUsername(String username)`, который возвращает объект `UserDetails`.

Spring Security использует его для получения учётных данных пользователя при аутентификации. Сам `UserDetailsService` **не проверяет пароль** — только загружает данные. Сравнение паролей выполняет `DaoAuthenticationProvider`.

---

### 4. Для чего используется PasswordEncoder?

`PasswordEncoder` шифрует пароль при регистрации и проверяет его при входе. В нашем проекте используется `BCryptPasswordEncoder` — он генерирует солёный хэш пароля.

Зачем: нельзя хранить пароли в открытом виде — при утечке базы злоумышленник сразу получит все пароли. BCrypt делает невозможным обратное преобразование хэша в пароль.

---

### 5. Почему пароли нельзя хранить в открытом виде?

- При утечке базы данных все пароли сразу скомпрометированы
- Пользователи часто используют одинаковые пароли на разных сайтах
- Администраторы БД могут видеть пароли пользователей
- Требования регуляторов (GDPR, ФЗ-152) запрещают хранение паролей в открытом виде

Хэширование через BCrypt решает эти проблемы: даже при утечке хэша восстановить пароль практически невозможно.

---

### 6. Какую роль выполняет AuthenticationManager?

`AuthenticationManager` — центральный компонент, запускающий процесс аутентификации. Он принимает объект `Authentication` (с логином и паролем) и возвращает полностью аутентифицированный объект или выбрасывает исключение.

В нашем проекте он используется в `AuthController.login()` для ручной аутентификации при выдаче JWT.

---

### 7. Что делает DaoAuthenticationProvider?

`DaoAuthenticationProvider` — конкретная реализация `AuthenticationProvider`, которая:
1. Получает логин из `Authentication`
2. Через `UserDetailsService` загружает пользователя из базы данных
3. Через `PasswordEncoder` сравнивает введённый пароль с хэшем из базы
4. Если совпадает — возвращает аутентифицированный объект

---

### 8. Как работает HTTP Basic?

1. Клиент отправляет заголовок `Authorization: Basic <base64(login:password)>`
2. Spring Security декодирует Base64 и извлекает логин и пароль
3. `AuthenticationManager` запускает проверку
4. `DaoAuthenticationProvider` обращается к `CustomUserDetailsService`
5. `PasswordEncoder` сравнивает пароль с хэшем в базе
6. Если проверка успешна — пользователь аутентифицирован

---

### 9. Что хранится в SecurityContext?

`SecurityContext` хранит объект `Authentication`, который содержит:
- **Principal** — данные о текущем пользователе (обычно `UserDetails`)
- **Credentials** — учётные данные (пароль, обычно null после аутентификации)
- **Authorities** — коллекция ролей/разрешений (например, `ROLE_USER`)

Доступ: `SecurityContextHolder.getContext().getAuthentication()`

---

### 10. Как работает аутентификация через сессию?

1. Пользователь отправляет логин и пароль
2. Spring Security проверяет их и создаёт объект `Authentication`
3. `Authentication` сохраняется в `SecurityContext`
4. Контекст помещается в HTTP-сессию на сервере
5. Клиент получает cookie `JSESSIONID`
6. При следующих запросах браузер автоматически отправляет cookie
7. Сервер восстанавливает пользователя из сессии по `JSESSIONID`

---

### 11. Что такое JWT и чем он отличается от сессии?

**JWT (JSON Web Token)** — самодостаточный токен, содержащий информацию о пользователе в закодированном виде. Состоит из трёх частей: Header, Payload, Signature.

| Критерий | Сессия | JWT |
|----------|--------|-----|
| Хранение состояния | На сервере | На клиенте (в токене) |
| Механизм | Cookie JSESSIONID | Заголовок Authorization: Bearer |
| Масштабируемость | Сложнее (общая сессия) | Проще (stateless) |
| Подходит для | Браузерные приложения | REST API, мобильные приложения |

---

### 12. Зачем нужен JWT-фильтр?

JWT-фильтр (`JwtAuthenticationFilter`) перехватывает каждый запрос и:
1. Извлекает токен из заголовка `Authorization: Bearer <token>`
2. Валидирует токен (подпись, срок действия)
3. Извлекает username из токена
4. Загружает пользователя через `UserDetailsService`
5. Создаёт объект `Authentication` и помещает его в `SecurityContext`

Без фильтра сервер не смог бы распознать аутентифицированного пользователя по JWT.

---

### 13. Что делает addFilterBefore() в конфигурации?

`addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)` — добавляет наш JWT-фильтр в цепочку фильтров Spring Security **перед** стандартным `UsernamePasswordAuthenticationFilter`.

Это значит, что JWT-фильтр сработает раньше и, если токен валиден, установит аутентификацию в контекст ещё до того, как дойдёт очередь до стандартного фильтра.

---

### 14. В чём разница между SessionCreationPolicy.IF_REQUIRED и SessionCreationPolicy.STATELESS?

| Политика | Поведение |
|----------|-----------|
| `IF_REQUIRED` | Spring создаёт сессию, если она нужна (поведение по умолчанию). Подходит для классических веб-приложений. |
| `STATELESS` | Spring **никогда** не создаёт сессию и не использует её. Каждый запрос обрабатывается независимо. Подходит для REST API с JWT. |

В нашем проекте используется `STATELESS`, так как состояние пользователя передаётся через JWT-токен в каждом запросе.

---

### 15. Как проверить доступ к URL по роли пользователя?

**Способ 1 — через SecurityFilterChain (URL-based):**
```java
.requestMatchers("/admin/**").hasRole("ADMIN")
.requestMatchers("/users/**").hasAnyRole("USER", "ADMIN")
```

**Способ 2 — через аннотацию на метод (method-based):**
```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) { ... }
```

Для работы метода нужна аннотация `@EnableMethodSecurity` на классе конфигурации.

Важно: в коде роль хранится как `ROLE_ADMIN`, но в методах `hasRole("ADMIN")` префикс `ROLE_` добавляется автоматически.

---

## Краткий справочник по ключевым классам проекта

| Класс | Пакет | Назначение |
|-------|-------|------------|
| `UserRole` | model.enums | Enum ролей: ROLE_USER, ROLE_ADMIN |
| `User` | model.entity | Сущность с полями password, role |
| `RegisterRequest` | model.dto | DTO регистрации (name, email, password) |
| `LoginRequest` | model.dto | DTO логина (email, password) |
| `CustomUserDetails` | security | Адаптер User → UserDetails |
| `CustomUserDetailsService` | security | Загрузка пользователя по email |
| `JwtService` | security | Генерация, валидация, парсинг JWT |
| `JwtAuthenticationFilter` | security | Фильтр извлечения JWT из запроса |
| `SecurityConfig` | config | Конфигурация Spring Security |
| `AuthService` | service | Регистрация пользователей |
| `AuthController` | controller | Эндпоинты /auth/register, /auth/login |
| `AdminController` | controller | Эндпоинт /admin/ping (только ADMIN) |

## Ключевые эндпоинты

| Метод | URL | Доступ | Описание |
|-------|-----|--------|----------|
| POST | /auth/register | Все | Регистрация пользователя |
| POST | /auth/register-admin | Все | Регистрация администратора |
| POST | /auth/login | Все | Логин, возвращает JWT |
| GET | /admin/ping | ADMIN | Проверка роли администратора |
| GET | /users/all | USER, ADMIN | Список всех пользователей |
| GET | /notifications/all | USER, ADMIN | Список уведомлений |
| DELETE | /users/{id} | ADMIN | Удаление пользователя (@PreAuthorize) |
