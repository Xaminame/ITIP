# Отчет по лабораторной работе №1
## «Инструмент сборки Maven»

**Дисциплина:** Информационные технологии и программирование

---

## Цель работы

Ознакомиться с инструментом автоматической сборки проектов на Java — Maven. Создать проект на Maven, настроить зависимости, логирование, работу с JSON и статический анализ кода.

---

## Ход выполнения работы

### Этап 1. Создание базового проекта и настройка exec-maven-plugin

На данном этапе был создан базовый Maven проект со следующей структурой:

```
Test-Maven/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── org/
│       │       └── example/
│       │           ├── Main.java
│       │           ├── Person.java
│       │           └── JsonSerializer.java
│       └── resources/
│           └── logback.xml
```

В файл `pom.xml` добавлен плагин `exec-maven-plugin` для запуска приложения:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.1.0</version>
    <configuration>
        <mainClass>org.example.Main</mainClass>
        <skip>false</skip>
    </configuration>
</plugin>
```

**Команда для сборки и запуска проекта:**
```bash
mvn clean install exec:java
```

---

### Этап 2. Добавление зависимости для логирования

Для логирования выбран SLF4J с реализацией Logback. В `pom.xml` добавлены зависимости:

```xml
<!-- SLF4J API для логирования -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.9</version>
</dependency>

<!-- Logback - реализация SLF4J -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.14</version>
</dependency>
```

**Преимущества SLF4J:**
- Абстракция над различными фреймворками логирования
- Поддержка параметризованных сообщений (placeholder `{}`)
- Возможность смены реализации без изменения кода

---

### Этап 3. Модификация класса Main для использования логирования

Класс `Main` был модифицирован для использования логирования вместо `System.out`:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            logger.debug("Итерация цикла: i = {}", i);
        }
    }
}
```

Также создан конфигурационный файл `logback.xml` в директории `src/main/resources/`.

---

### Этап 4. Добавление зависимости для работы с JSON

Для работы с JSON добавлена библиотека Jackson:

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.16.1</version>
</dependency>
```

**Команда для просмотра дерева зависимостей:**
```bash
mvn dependency:tree
```

---

### Этап 5. Создание класса для сериализации/десериализации JSON

Созданы классы:

1. **Person.java** — класс-модель данных с полями `name`, `age`, `email`
2. **JsonSerializer.java** — утилитарный класс для работы с JSON

Класс `JsonSerializer` предоставляет методы:
- `toJson(Object object)` — сериализация объекта в JSON строку
- `toJsonPretty(Object object)` — сериализация с форматированием
- `fromJson(String json, Class<T> clazz)` — десериализация JSON в объект

**Транзитивные зависимости Jackson:**
При добавлении `jackson-databind` автоматически подтягиваются:
- `jackson-core` — низкоуровневый API для парсинга JSON
- `jackson-annotations` — аннотации для настройки сериализации

---

### Этап 6. Добавление плагина SpotBugs

В секцию `<build>` добавлен плагин SpotBugs для статического анализа кода:

```xml
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.8.3.0</version>
    <configuration>
        <threshold>Medium</threshold>
        <effort>Max</effort>
        <failOnError>true</failOnError>
    </configuration>
</plugin>
```

**Команды для работы со SpotBugs:**

```bash
# Проверка кода на наличие ошибок
mvn spotbugs:check

# Генерация HTML-отчета
mvn spotbugs:spotbugs

# Запуск GUI для просмотра результатов
mvn spotbugs:gui
```

**Параметры конфигурации:**
- `threshold` — минимальный уровень приоритета ошибок (High, Medium, Low)
- `effort` — уровень глубины анализа (Min, Default, Max)
- `failOnError` — прерывание сборки при обнаружении ошибок

---

## Команды Maven для работы с проектом

| Команда | Описание |
|---------|----------|
| `mvn clean` | Очистка директории target |
| `mvn compile` | Компиляция исходного кода |
| `mvn test` | Запуск тестов |
| `mvn package` | Упаковка проекта в JAR |
| `mvn install` | Установка в локальный репозиторий |
| `mvn clean install` | Полная сборка с очисткой |
| `mvn clean install exec:java` | Сборка и запуск приложения |
| `mvn dependency:tree` | Просмотр дерева зависимостей |
| `mvn spotbugs:check` | Проверка кода SpotBugs |

---

## Контрольные вопросы

### 1. Что такое Apache Maven и для чего он используется?

Apache Maven — это инструмент автоматизации сборки проектов на основе концепции Project Object Model (POM). Используется для:
- Управления зависимостями проекта
- Автоматизации процесса сборки
- Стандартизации структуры проекта
- Управления жизненным циклом проекта
- Генерации отчетов и документации

### 2. Как установить Maven на различные операционные системы?

**Linux (Debian/Ubuntu):**
```bash
sudo apt install maven
```

**macOS (Homebrew):**
```bash
brew install maven
```

**Windows:**
1. Скачать архив с официального сайта
2. Распаковать в удобную директорию
3. Добавить путь к `bin` в переменную PATH
4. Установить переменную MAVEN_HOME

### 3. Какова структура проекта Maven?

```
project/
├── pom.xml                    # Конфигурация проекта
├── src/
│   ├── main/
│   │   ├── java/              # Исходный код
│   │   └── resources/         # Ресурсы
│   └── test/
│       ├── java/              # Тесты
│       └── resources/         # Тестовые ресурсы
└── target/                    # Результаты сборки
```

### 4. Что такое POM файл и какова его роль в проекте Maven?

POM (Project Object Model) — XML-файл, содержащий информацию о проекте и конфигурацию для Maven. Роль POM:
- Идентификация проекта (GAV)
- Описание зависимостей
- Конфигурация плагинов
- Настройка процесса сборки
- Определение свойств проекта

### 5. Какова структура файла POM?

Основные элементы:
- `<project>` — корневой элемент
- `<modelVersion>` — версия POM модели (4.0.0)
- `<groupId>`, `<artifactId>`, `<version>` — координаты проекта
- `<packaging>` — тип упаковки (jar, war, pom)
- `<properties>` — свойства проекта
- `<dependencies>` — зависимости
- `<build>` — настройки сборки
- `<plugins>` — плагины

### 6. Что такое зависимости (dependencies) в Maven и как они определяются?

Зависимости — это внешние библиотеки, необходимые проекту. Определяются в секции `<dependencies>` через GAV координаты:

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.9</version>
</dependency>
```

### 7. Что такое репозиторий Maven и какие виды репозиториев существуют?

Репозиторий Maven — хранилище артефактов (JAR, POM и др.).

**Виды:**
- **Локальный** — `~/.m2/repository`, кэш загруженных зависимостей
- **Центральный** — Maven Central, основной публичный репозиторий
- **Удаленный** — корпоративные репозитории (Nexus, Artifactory)

### 8. Как добавить зависимость в проект Maven?

Добавить в `pom.xml` внутри тега `<dependencies>`:

```xml
<dependencies>
    <dependency>
        <groupId>группа</groupId>
        <artifactId>артефакт</artifactId>
        <version>версия</version>
    </dependency>
</dependencies>
```

### 9. Что такое плагины в Maven и как они используются?

Плагины — компоненты, выполняющие задачи в процессе сборки. Каждый плагин содержит цели (goals). Примеры:
- `maven-compiler-plugin` — компиляция кода
- `maven-surefire-plugin` — запуск тестов
- `exec-maven-plugin` — запуск Java приложений
- `spotbugs-maven-plugin` — статический анализ

### 10. Как создать новый проект Maven с помощью команды Maven?

```bash
mvn archetype:generate \
    -DgroupId=org.example \
    -DartifactId=my-app \
    -DarchetypeArtifactId=maven-archetype-quickstart \
    -DinteractiveMode=false
```

### 11. Что такое цели (goals) и фазы (phases) в Maven и в чем их отличие?

**Фазы (phases)** — этапы жизненного цикла сборки (compile, test, package, install, deploy). Выполняются последовательно.

**Цели (goals)** — конкретные задачи плагинов (compiler:compile, surefire:test). Привязываются к фазам.

Отличие: фаза — абстрактный этап, цель — конкретное действие плагина.

### 12. Как выполнить команду сборки проекта в Maven?

```bash
mvn clean install
```
Эта команда очищает предыдущую сборку и выполняет все фазы до install.

### 13. Что такое жизненный цикл сборки (build lifecycle) в Maven?

Последовательность фаз для сборки проекта. Основные циклы:
- **default** — основная сборка (validate → compile → test → package → verify → install → deploy)
- **clean** — очистка (pre-clean → clean → post-clean)
- **site** — генерация документации

### 14. Как настроить профили (profiles) в Maven для разных сред?

```xml
<profiles>
    <profile>
        <id>development</id>
        <properties>
            <env>dev</env>
        </properties>
    </profile>
    <profile>
        <id>production</id>
        <properties>
            <env>prod</env>
        </properties>
    </profile>
</profiles>
```

Активация: `mvn install -P production`

### 15. Как управлять версиями зависимостей в Maven?

- Указывать точные версии в `<version>`
- Использовать `<dependencyManagement>` для централизованного управления
- Применять свойства: `<version>${jackson.version}</version>`
- Использовать BOM (Bill of Materials)

### 16. Что такое «SNAPSHOT» версии в Maven и как они используются?

SNAPSHOT — версия в разработке (например, `1.0-SNAPSHOT`). Особенности:
- Maven всегда проверяет обновления
- Артефакт может изменяться
- Используется для разработки
- При релизе SNAPSHOT удаляется

### 17. Как использовать Maven для создания отчета о качестве кода?

```bash
# Генерация сайта с отчетами
mvn site

# SpotBugs отчет
mvn spotbugs:spotbugs

# Checkstyle отчет
mvn checkstyle:checkstyle
```

### 18. Какие команды Maven используются для очистки проекта, сборки, тестирования и установки?

- **Очистка:** `mvn clean`
- **Компиляция:** `mvn compile`
- **Тестирование:** `mvn test`
- **Упаковка:** `mvn package`
- **Установка:** `mvn install`
- **Все вместе:** `mvn clean install`

### 19. Как интегрировать Maven с системой контроля версий, такой как Git?

1. Добавить `.gitignore` с исключениями:
   ```
   target/
   *.class
   *.jar
   .idea/
   ```
2. Использовать maven-release-plugin для релизов
3. Хранить pom.xml в репозитории
4. CI/CD интеграция (GitHub Actions, GitLab CI)

### 20. Как добавить и настроить сторонний репозиторий в проекте Maven?

```xml
<repositories>
    <repository>
        <id>my-repo</id>
        <url>https://repo.example.com/maven</url>
    </repository>
</repositories>
```

### 21. Какие скоупы зависимостей существуют в Maven и для чего они используются?

- **compile** (по умолчанию) — доступна везде, включается в сборку
- **provided** — предоставляется контейнером (например, servlet-api)
- **runtime** — нужна только при выполнении
- **test** — только для тестов
- **system** — локальный JAR файл
- **import** — импорт BOM в dependencyManagement

### 22. Чем отличается плагин от зависимости в Maven?

| Плагин | Зависимость |
|--------|-------------|
| Выполняет задачи сборки | Используется в коде приложения |
| Работает во время сборки | Включается в classpath |
| Секция `<plugins>` | Секция `<dependencies>` |
| Пример: maven-compiler-plugin | Пример: jackson-databind |

### 23. Как работает транзитивность зависимостей?

Если A зависит от B, а B зависит от C, то Maven автоматически подключит C к проекту A. Это называется транзитивной зависимостью. Maven разрешает конфликты версий и формирует итоговое дерево зависимостей.

### 24. Для чего нужен плагин surefire?

Maven Surefire Plugin — плагин для запуска модульных тестов. Автоматически находит и выполняет тесты в `src/test/java`. Поддерживает JUnit, TestNG. Генерирует отчеты в `target/surefire-reports`.

### 25. Как исключить транзитивную зависимость?

```xml
<dependency>
    <groupId>org.example</groupId>
    <artifactId>example-lib</artifactId>
    <version>1.0</version>
    <exclusions>
        <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

---

## Вывод

В ходе лабораторной работы был создан Maven проект с настроенными зависимостями для логирования (SLF4J + Logback) и работы с JSON (Jackson). Изучены основные концепции Maven: структура POM-файла, управление зависимостями, использование плагинов, жизненный цикл сборки. Настроен статический анализатор кода SpotBugs для повышения качества кода.
