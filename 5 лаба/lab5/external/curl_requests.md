# Curl-запросы к сервису уведомлений

Базовый URL: `http://localhost:8080`

---

## 1. Аутентификация (AuthController)

### Регистрация пользователя

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Иван Иванов",
    "email": "ivan@example.com",
    "password": "password123"
  }'
```

### Регистрация администратора

```bash
curl -X POST http://localhost:8080/auth/register-admin \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Админ",
    "email": "admin@example.com",
    "password": "admin123"
  }'
```

### Логин (получение JWT-токена)

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ivan@example.com",
    "password": "password123"
  }'
```

> Ответ содержит JWT-токен. Используйте его в заголовке `Authorization` для последующих запросов:
> `Authorization: Bearer <token>`

---

## 2. Пользователи (UserController)

### Получить всех пользователей

```bash
curl -X GET http://localhost:8080/users/all \
  -H "Authorization: Bearer <token>"
```

### Получить пользователя по ID

```bash
curl -X GET http://localhost:8080/users/1 \
  -H "Authorization: Bearer <token>"
```

### Создать пользователя

```bash
curl -X POST http://localhost:8080/users/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "name": "Петр Петров",
    "email": "petr@example.com",
    "phone": "+79001234567",
    "deviceToken": "firebase-token-123",
    "telegramChatId": "123456789"
  }'
```

### Обновить пользователя

```bash
curl -X PUT http://localhost:8080/users/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "name": "Иван Обновленный",
    "email": "ivan_new@example.com",
    "phone": "+79009876543",
    "deviceToken": "new-firebase-token",
    "telegramChatId": "987654321"
  }'
```

### Удалить пользователя

```bash
curl -X DELETE http://localhost:8080/users/1 \
  -H "Authorization: Bearer <token>"
```

---

## 3. Уведомления (NotificationController)

### Получить все уведомления

```bash
curl -X GET http://localhost:8080/notifications/all \
  -H "Authorization: Bearer <token>"
```

### Получить уведомление по ID

```bash
curl -X GET http://localhost:8080/notifications/1 \
  -H "Authorization: Bearer <token>"
```

### Создать уведомление

```bash
curl -X POST http://localhost:8080/notifications/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "recipientId": 1,
    "title": "Тестовое уведомление",
    "message": "Текст уведомления",
    "channel": "EMAIL"
  }'
```

> Допустимые значения `channel`: `EMAIL`, `SMS`, `PUSH`, `TELEGRAM`

### Обновить уведомление

```bash
curl -X PUT http://localhost:8080/notifications/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "recipientId": 1,
    "title": "Обновленное уведомление",
    "message": "Обновленный текст",
    "channel": "SMS"
  }'
```

### Удалить уведомление

```bash
curl -X DELETE http://localhost:8080/notifications/1 \
  -H "Authorization: Bearer <token>"
```

### Получить уведомления по статусу

```bash
curl -X GET http://localhost:8080/notifications/status/CREATED \
  -H "Authorization: Bearer <token>"
```

> Допустимые значения `status`: `CREATED`, `SENT`, `FAILED`

### Получить уведомления по каналу

```bash
curl -X GET http://localhost:8080/notifications/channel/EMAIL \
  -H "Authorization: Bearer <token>"
```

### Получить уведомления по ID получателя

```bash
curl -X GET http://localhost:8080/notifications/recipient/1 \
  -H "Authorization: Bearer <token>"
```

---

## 4. Админ-панель (AdminController)

### Проверка доступа администратора

```bash
curl -X GET http://localhost:8080/admin/ping \
  -H "Authorization: Bearer <admin_token>"
```

---

## 5. Общие эндпоинты (HelloController)

### Приветствие

```bash
curl -X GET http://localhost:8080/hello
```

### Прощание

```bash
curl -X GET http://localhost:8080/goodbye
```

### Приветствие по имени

```bash
curl -X GET "http://localhost:8080/greet?name=Иван"
```

### Информация о пользователе

```bash
curl -X GET "http://localhost:8080/info?name=Иван&age=25"
```
