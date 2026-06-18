# Лабораторная работа №4 — Spring Boot + Spring Data JPA + PostgreSQL

## Инструкция по запуску

### Предварительные требования
- Java 17+
- Maven
- PostgreSQL (запущенный локально или удаленно)
- Postman / curl (для тестирования API)

### Шаги запуска

1. **Создать базу данных PostgreSQL:**
   ```sql
   CREATE DATABASE demo;
   ```

2. **Настроить подключение** в файле `.env` (корень проекта):
   ```
   DB_URL=jdbc:postgresql://localhost:5432/demo
   DB_USERNAME=postgres
   DB_PASSWORD=postgres
   ```

3. **Запустить приложение:**
   ```bash
   ./mvnw spring-boot:run
   ```
   Или через IntelliJ IDEA: запустить `DemoApplication.java` (добавить переменные окружения из `.env` в Run Configuration -> Environment variables).

4. **Проверить работу:** открыть Postman и отправить запрос:
   ```
   GET http://localhost:8080/users/all
   ```

---

## Шпаргалка для сдачи

### Структура проекта
```
spring_lab3_notifications.demo
 ├── config/          — AppConfig (конфигурация Spring)
 ├── controller/      — UserController, NotificationController (REST API)
 ├── service/         — UserService, NotificationService (бизнес-логика)
 ├── repository/      — UserRepository, NotificationRepository (доступ к данным)
 └── model/
      ├── dto/        — UserDto, NotificationDto (объекты передачи данных)
      ├── entity/     — User, Notification (JPA-сущности)
      └── enums/      — NotificationChannel, NotificationStatus
```

### Ключевые файлы и их назначение

| Файл | Назначение |
|------|-----------|
| `pom.xml` | Зависимости: spring-boot-starter-data-jpa, postgresql, lombok, validation, dotenv-java |
| `application.properties` | Подключение к БД через переменные окружения, ddl-auto=update, show-sql=true |
| `.env` | DB_URL, DB_USERNAME, DB_PASSWORD |
| `DemoApplication.java` | Точка входа, загрузка .env через dotenv-java |

### API-эндпоинты

**Users:**
| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/users/add` | Создать пользователя |
| GET | `/users/all` | Список всех пользователей |
| GET | `/users/{id}` | Получить пользователя по ID |
| PUT | `/users/{id}` | Обновить пользователя |
| DELETE | `/users/{id}` | Удалить пользователя |

**Notifications:**
| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/notifications/add` | Создать уведомление |
| GET | `/notifications/all` | Список всех уведомлений |
| GET | `/notifications/{id}` | Получить уведомление по ID |
| PUT | `/notifications/{id}` | Обновить уведомление |
| DELETE | `/notifications/{id}` | Удалить уведомление |
| GET | `/notifications/status/{status}` | Фильтр по статусу |
| GET | `/notifications/channel/{channel}` | Фильтр по каналу |
| GET | `/notifications/recipient/{recipientId}` | Уведомления получателя |

### Тестовые JSON

**Создание пользователя (POST /users/add):**
```json
{
  "name": "Иван Иванов",
  "email": "ivan@example.com",
  "phone": "+79990001122",
  "deviceToken": "device-token-123",
  "telegramChatId": "123456789"
}
```

**Создание уведомления (POST /notifications/add):**
```json
{
  "title": "Напоминание",
  "message": "Завтра состоится занятие по Spring Data",
  "channel": "EMAIL",
  "recipientId": 1
}
```

**Обновление уведомления со статусом SENT (PUT /notifications/{id}):**
```json
{
  "title": "Напоминание",
  "message": "Занятие перенесено",
  "channel": "EMAIL",
  "status": "SENT",
  "recipientId": 1
}
```

### Ответы на контрольные вопросы

**1. Что такое Spring Data JPA и зачем он нужен?**
Spring Data JPA — модуль Spring, упрощающий работу с базами данных. Позволяет описывать сущности и создавать интерфейсы-репозитории, наследуемые от `JpaRepository`, вместо написания шаблонного кода для CRUD-операций.

**2. Какую роль выполняет Hibernate?**
Hibernate — ORM-фреймворк, реализующий спецификацию JPA. Отвечает за маппинг Java-объектов на таблицы БД, генерацию SQL-запросов, работу со связями между сущностями и управление транзакциями.

**3. Что делает JpaRepository?**
`JpaRepository<Entity, ID>` — интерфейс Spring Data, предоставляющий готовые методы: `save()`, `findById()`, `findAll()`, `deleteById()` и другие. Также поддерживает создание запросов через имена методов (derived queries).

**4. Как реализуется связь один-ко-многим между User и Notification?**
- В `User`: `@OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)` — один пользователь имеет много уведомлений.
- В `Notification`: `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "recipient_id")` — каждое уведомление принадлежит одному пользователю.
- `mappedBy` указывает, что владельцем связи является поле `recipient` в Notification.

**5. Что делает @ManyToOne?**
Аннотация устанавливает связь "многие к одному" — множество Notification может принадлежать одному User. `FetchType.LAZY` означает отложенную загрузку — связанный объект загружается из БД только при обращении к нему.

**6. Как работает @Transactional?**
`@Transactional` оборачивает метод в транзакцию БД. Если метод выполняется успешно — транзакция фиксируется (commit). Если выбрасывается исключение — откатывается (rollback). `readOnly = true` оптимизирует запросы на чтение.

**7. В чем разница между JPQL и native SQL в @Query?**
- **JPQL** работает с Java-сущностями и их полями: `SELECT n FROM Notification n WHERE n.status = :status`.
- **Native SQL** работает напрямую с таблицами и колонками БД: `SELECT * FROM notifications WHERE status = :status` (с параметром `nativeQuery = true`).
- JPQL переносим между СУБД, native SQL — привязан к конкретной СУБД.

**8. Что делает @Valid в контроллере?**
Указывает Spring выполнить валидацию входного объекта (DTO) перед вызовом метода. Если данные не соответствуют ограничениям (@NotBlank, @Email и т.д.), Spring вернет 400 Bad Request без выполнения метода.

**9. Для чего @NotBlank, @Email, @Pattern?**
- `@NotBlank` — строка не null, не пустая, не только пробелы.
- `@Email` — проверка формата email.
- `@Pattern(regexp = "...")` — проверка строки на соответствие регулярному выражению (например, формат телефона).

**10. Почему DTO удобнее валидировать, чем JPA-сущности?**
- DTO — это контракт API, сущность — модель БД. Они могут иметь разные ограничения.
- Валидация в DTO срабатывает до сервисного слоя, что позволяет вернуть понятную ошибку клиенту.
- Сущность может содержать поля, которые заполняются автоматически (id, createdAt) и не должны валидироваться на входе.
- Разделение ответственности: DTO отвечает за входные данные, сущность — за хранение.

### Ключевые аннотации (шпаргалка)

| Аннотация | Слой | Назначение |
|-----------|------|-----------|
| `@Entity` | Model | Класс = таблица БД |
| `@Table(name = "...")` | Model | Имя таблицы |
| `@Id` | Model | Первичный ключ |
| `@GeneratedValue(IDENTITY)` | Model | Автоинкремент |
| `@Column(nullable = false)` | Model | NOT NULL в БД |
| `@OneToMany` / `@ManyToOne` | Model | Связи между таблицами |
| `@Enumerated(EnumType.STRING)` | Model | Хранение enum как строки |
| `@Repository` | Repository | Компонент доступа к данным |
| `@Service` | Service | Компонент бизнес-логики |
| `@RestController` | Controller | REST-контроллер |
| `@Transactional` | Service | Управление транзакциями |
| `@Valid` | Controller | Запуск валидации DTO |
| `@NotBlank`, `@Email`, `@Pattern` | DTO | Правила валидации полей |
| `@RequiredArgsConstructor` | Все слои | DI через конструктор (Lombok) |
| `@Query` | Repository | Кастомный JPQL/SQL запрос |

---

## Отчет о выполнении лабораторной работы №4

### Тема
Разработка веб-приложения на Spring Boot. Работа с данными.

### Цель работы
Научиться работать с данными в Spring Boot-приложении с использованием Spring Data JPA и Hibernate: подключать приложение к базе данных PostgreSQL, создавать JPA-сущности и репозитории, реализовывать CRUD-операции, настраивать связи между таблицами, использовать транзакции, валидировать входные данные.

### Часть 0. Подготовка
- Установлены IntelliJ IDEA, PostgreSQL, DBeaver, Postman.
- Используется проект `spring-lab3-notifications` из предыдущей лабораторной.
- Создана база данных `demo` в PostgreSQL.

### Часть 1. Подключение Spring Data JPA и PostgreSQL
В `pom.xml` добавлены зависимости:
- `spring-boot-starter-data-jpa` — Spring Data JPA + Hibernate
- `postgresql` — JDBC-драйвер
- `lombok` — генерация шаблонного кода
- `spring-boot-starter-validation` — Bean Validation
- `dotenv-java` — загрузка переменных окружения из .env

В `application.properties` настроено подключение к БД через переменные окружения `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`. Включен `ddl-auto=update` для автоматического создания таблиц, `show-sql=true` для логирования SQL.

Переменные окружения хранятся в `.env` файле и загружаются через библиотеку `dotenv-java` в `DemoApplication.main()`.

### Часть 2. Создание сущностей и перечислений
Созданы перечисления:
- `NotificationChannel` — EMAIL, SMS, PUSH, TELEGRAM
- `NotificationStatus` — CREATED, SENT, FAILED

Созданы JPA-сущности:
- `User` (`@Entity`, таблица `users`) — поля: id, name, email, phone, deviceToken, telegramChatId, createdAt. Связь `@OneToMany` с Notification.
- `Notification` (`@Entity`, таблица `notifications`) — поля: id, title, message, channel, status, createdAt, sentAt. Связь `@ManyToOne` с User через поле `recipient`.

Использованы аннотации: `@Id`, `@GeneratedValue(IDENTITY)`, `@Column`, `@Enumerated(EnumType.STRING)`, `@JoinColumn`, `@PrePersist`.

### Часть 3. Создание DTO и репозиториев
Созданы DTO:
- `UserDto` — с валидацией полей name, email, phone.
- `NotificationDto` — с валидацией title, message, channel, recipientId.

Созданы репозитории:
- `UserRepository extends JpaRepository<User, Long>`
- `NotificationRepository extends JpaRepository<Notification, Long>` — с дополнительными методами поиска.

### Часть 4. CRUD для User
Реализован `UserService` с методами: createUser, getAllUsers, getUserById, updateUser, deleteUser. Все методы аннотированы `@Transactional`. Используется приватный метод `mapToDto()` для преобразования Entity -> DTO.

Реализован `UserController` с эндпоинтами:
- POST `/users/add`, GET `/users/all`, GET `/users/{id}`, PUT `/users/{id}`, DELETE `/users/{id}`

### Часть 5. CRUD для Notification
Реализован `NotificationService` с CRUD-методами и дополнительными методами фильтрации. Преобразование в DTO вынесено в приватный метод `mapToDto()`.

Реализован `NotificationController` с эндпоинтами для CRUD и фильтрации по status, channel, recipientId.

### Часть 6. Методы репозитория Spring Data JPA
В `NotificationRepository` добавлены:
- `findByStatusAndChannel()` — запрос по двум параметрам (derived query)
- `findAllByOrderByCreatedAtDesc()` — сортировка по дате убывания
- `@Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.status = :status")` — кастомный JPQL-запрос

### Часть 7. Транзакции
Все сервисные методы аннотированы `@Transactional`:
- Методы чтения: `@Transactional(readOnly = true)` — оптимизация для запросов на чтение
- Методы записи: `@Transactional` — гарантия целостности данных, откат при исключениях

### Часть 9. Валидация данных
В `UserDto` добавлены аннотации:
- `@NotBlank` + `@Size(max = 100)` для name
- `@NotBlank` + `@Email` + `@Pattern` для email
- `@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")` для phone

В `NotificationDto`:
- `@NotBlank` для title и message
- `@NotNull` для channel и recipientId

В контроллерах используется `@Valid` для запуска валидации.

### Выполненные самостоятельные задания
1. Метод `mapToDto()` вынесен в сервисы (UserService, NotificationService).
2. Проверка телефона с помощью `@Pattern` реализована в UserDto.
3. Метод `findByStatusAndChannel()` добавлен в NotificationRepository.
4. Метод `findAllByOrderByCreatedAtDesc()` добавлен для сортировки.
5. JPQL-запрос `findByRecipientIdAndStatus()` реализован через `@Query`.
6. В `updateNotification()` при статусе SENT автоматически устанавливается sentAt.

### Вывод
В ходе лабораторной работы было реализовано Spring Boot-приложение системы уведомлений с полной поддержкой работы с данными через Spring Data JPA и PostgreSQL. Освоены: создание JPA-сущностей с связями OneToMany/ManyToOne, работа с JpaRepository (включая derived queries и @Query), управление транзакциями через @Transactional, валидация входных данных через Bean Validation. Приложение поддерживает полный набор CRUD-операций для пользователей и уведомлений, а также дополнительные методы фильтрации и сортировки.
