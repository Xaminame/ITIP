# Отчёт по лабораторной работе №2
## Инструмент сборки Gradle

---

## Выполненные задания

### 1. Создание проекта Gradle

Создан проект Gradle со стандартной структурой:
- `build.gradle.kts` — основной файл конфигурации сборки
- `settings.gradle.kts` — настройки проекта
- `src/main/java/` — исходный код
- `src/main/resources/` — ресурсы
- `src/test/` — тестовый код

Добавлен плагин `application` с указанием главного класса `org.example.Main`.

### 2. Добавление зависимостей

В `build.gradle.kts` добавлены зависимости:

```kotlin
dependencies {
    // Apache Commons Lang3 для работы со строками
    implementation("org.apache.commons:commons-lang3:3.14.0")

    // Стек логирования
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("org.slf4j:slf4j-api:2.0.9")
}
```

### 3. Создание класса Main

Создан класс `Main.java` с:
- Инициализацией логгера SLF4J
- Запросом ввода строки у пользователя
- Обработкой строки с использованием `StringUtils` из Apache Commons Lang3:
  - `reverse()` — реверс строки
  - `capitalize()` — первая буква заглавная
  - `upperCase()` — верхний регистр
  - `swapCase()` — инверсия регистра
  - `isBlank()`, `isNumeric()`, `isAlpha()` — проверки строки
- Логированием начала и завершения работы программы

### 4. Плагин Shadow для Fat JAR

Добавлен плагин Shadow для создания исполняемого JAR со всеми зависимостями:

```kotlin
plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

tasks.shadowJar {
    manifest {
        attributes(Pair("Main-Class", "org.example.Main"))
    }
}
```

### 5. Пользовательские задачи Gradle

Созданы две пользовательские задачи:

**PrintInfoTask** — выводит информацию о проекте:
```kotlin
abstract class PrintInfoTask : DefaultTask() {
    @TaskAction
    fun print() {
        println("Проект: ${project.name}")
        println("Версия Gradle: ${project.gradle.version}")
    }
}
```

**GenerateBuildPassportTask** — генерирует файл `build-passport.properties` с информацией:
- Имя пользователя (из `USER` / `USERNAME`)
- Название ОС
- Версия Java
- Дата и время сборки
- Приветственное сообщение

Задача `generateBuildPassport` интегрирована в процесс сборки через зависимость:
```kotlin
tasks.named("processResources") {
    dependsOn(tasks.named("generateBuildPassport"))
}
```

---

## Команды для сборки

| Команда | Описание |
|---------|----------|
| `gradle clean` | Очищает директорию сборки (`build/`) |
| `gradle build` | Компилирует код, запускает тесты, создаёт JAR |
| `gradle run` | Запускает приложение |
| `gradle clean build run` | Полная сборка с очисткой и запуском |
| `gradle shadowJar` | Создаёт Fat JAR со всеми зависимостями |
| `gradle tasks` | Показывает список всех доступных задач |
| `gradle printInfo` | Выводит информацию о проекте (кастомная задача) |
| `gradle generateBuildPassport` | Генерирует паспорт сборки (кастомная задача) |

**Запуск Fat JAR:**
```bash
java -jar build/libs/lab2-1.0-SNAPSHOT-all.jar
```

---

## Контрольные вопросы

### 1. Что такое Gradle и для чего он используется?

Gradle — это современный инструмент автоматизации сборки, написанный на Groovy/Kotlin. Используется для:
- Компиляции исходного кода
- Управления зависимостями
- Запуска тестов
- Упаковки приложений (JAR, WAR)
- Автоматизации процессов CI/CD

### 2. Чем отличается build.gradle.kts от settings.gradle.kts?

- **build.gradle.kts** — конфигурация сборки конкретного модуля: плагины, зависимости, задачи, настройки компиляции.
- **settings.gradle.kts** — настройки всего проекта: имя проекта, подключаемые модули, репозитории для плагинов.

### 3. Что такое implementation в блоке dependencies?

`implementation` — область видимости зависимости, которая:
- Доступна во время компиляции и выполнения
- Не транзитивна для других модулей (в отличие от `api`)
- Рекомендуется для большинства зависимостей

### 4. Для чего нужен плагин application?

Плагин `application`:
- Позволяет запускать Java-приложение через `gradle run`
- Указывает главный класс (`mainClass`)
- Создаёт дистрибутив с запускающими скриптами

### 5. Что делает плагин Shadow?

Плагин Shadow создаёт **Fat JAR** (uber-jar):
- Объединяет все зависимости в один JAR-файл
- Позволяет запускать приложение без установки зависимостей
- Полезен для деплоя и распространения приложений

### 6. Как создать пользовательскую задачу в Gradle?

```kotlin
abstract class MyTask : DefaultTask() {
    @TaskAction
    fun execute() {
        // Логика задачи
    }
}

tasks.register<MyTask>("myTaskName") {
    group = "Custom"
    description = "Описание задачи"
}
```

### 7. Что такое @TaskAction?

`@TaskAction` — аннотация, помечающая метод, который будет выполнен при запуске задачи. Это точка входа в логику задачи.

### 8. Как сделать зависимость между задачами?

```kotlin
tasks.named("taskB") {
    dependsOn(tasks.named("taskA"))
}
```
Теперь `taskA` будет выполнена перед `taskB`.

### 9. Зачем нужен логгер SLF4J вместо System.out.println?

SLF4J + Logback предоставляют:
- Уровни логирования (DEBUG, INFO, WARN, ERROR)
- Форматированный вывод с временными метками
- Гибкую конфигурацию (файл, консоль, удалённый сервер)
- Возможность отключения/включения логов без изменения кода

### 10. Что такое Fat JAR?

Fat JAR (uber-jar) — JAR-файл, содержащий:
- Скомпилированный код приложения
- Все зависимости (библиотеки)
- Манифест с указанием главного класса

Позволяет запускать приложение одной командой `java -jar`.

---

## Структура проекта

```
lab2/
├── build.gradle.kts          # Конфигурация сборки
├── settings.gradle.kts       # Настройки проекта
├── gradlew                   # Gradle wrapper (Linux/Mac)
├── gradlew.bat               # Gradle wrapper (Windows)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/example/
│   │   │       └── Main.java
│   │   └── resources/
│   │       ├── logback.xml
│   │       └── build-passport.properties (генерируется)
│   └── test/
└── build/                    # Результаты сборки (генерируется)
    └── libs/
        └── lab2-1.0-SNAPSHOT-all.jar
```
