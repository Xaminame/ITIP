# Ответы на вопросы защиты лабораторной работы №1

---

## 1. Билд проекта через терминал. Команды и их объяснение

**Команда для сборки и запуска:**
```bash
mvn clean install exec:java
```

**Разбор каждой части:**

| Команда | Что делает |
|---------|------------|
| `mvn` | Вызов Maven из командной строки |
| `clean` | Фаза очистки — удаляет директорию `target/` со всеми предыдущими результатами сборки |
| `install` | Фаза установки — компилирует код, запускает тесты, упаковывает в JAR и копирует в локальный репозиторий (`~/.m2/repository`) |
| `exec:java` | Цель (goal) плагина exec-maven-plugin — запускает main-класс приложения |

**Что физически удаляется при `clean`:**
- Директория `target/` целиком
- Внутри неё: скомпилированные `.class` файлы, JAR-файл, отчёты тестов, временные файлы сборки

---

## 2. Артефакты после сборки проекта

**Что появляется в директории `target/` после сборки:**

| Артефакт | После какой фазы | Описание |
|----------|------------------|----------|
| `target/classes/` | `compile` | Скомпилированные `.class` файлы (байт-код Java) |
| `target/test-classes/` | `test-compile` | Скомпилированные тестовые классы |
| `target/surefire-reports/` | `test` | Отчёты о выполнении тестов |
| `target/Test-Maven-1.0-SNAPSHOT.jar` | `package` | Упакованный JAR-файл приложения |
| `target/maven-status/` | `compile` | Служебная информация компилятора |
| `target/maven-archiver/` | `package` | Метаданные для архивирования |

**Физический процесс:**
1. Исходный код `.java` → компилятор `javac` → байт-код `.class`
2. Байт-код `.class` → упаковщик → архив `.jar`
3. JAR-файл → копирование в `~/.m2/repository`

---

## 3. Жизненный цикл сборки (Build Lifecycle)

**Определение:** Жизненный цикл сборки — это упорядоченная последовательность фаз, через которые проходит проект при сборке. Каждая фаза выполняет определённую задачу.

**Основные фазы в хронологическом порядке:**

```
1. validate    → Проверка корректности проекта и наличия необходимой информации
2. compile     → Компиляция исходного кода (.java → .class)
3. test        → Запуск модульных тестов
4. package     → Упаковка скомпилированного кода в JAR/WAR
5. verify      → Проверка качества и интеграционные тесты
6. install     → Установка пакета в локальный репозиторий
7. deploy      → Публикация в удалённый репозиторий
```

**Важно:** При выполнении любой фазы автоматически выполняются все предшествующие. Например, `mvn install` выполнит: validate → compile → test → package → verify → install.

---

## 4. GroupId в pom.xml

**Что такое GroupId:**
- Уникальный идентификатор организации или группы проектов
- Следует соглашению об именовании Java-пакетов (обратное доменное имя)
- Пример: `org.example`, `com.google`, `ru.university`

**Связь с файлами проекта:**
```
pom.xml:     <groupId>org.example</groupId>

Структура:   src/main/java/org/example/Main.java
                            ↑↑↑↑↑↑↑↑↑↑↑
                            Совпадает с groupId
```

**Почему папка создаётся с таким названием:**
- Это **соглашение Maven** — структура пакетов в `src/main/java/` должна соответствовать groupId
- IDE автоматически создаёт папки `org/example/` при создании проекта с groupId `org.example`
- Это обеспечивает уникальность классов и предотвращает конфликты имён

**Явное указание:** В Java-файлах указывается `package org.example;` — это и есть явная связь с groupId.

---

## 5. ArtifactId

**Что такое ArtifactId:**
- Уникальное имя проекта/модуля внутри группы
- Обычно совпадает с именем JAR-файла
- Пример: `Test-Maven`, `spring-core`, `jackson-databind`

**Результат:** GAV координаты (GroupId:ArtifactId:Version) однозначно идентифицируют артефакт:
```
org.example:Test-Maven:1.0-SNAPSHOT
```

---

## 6. SNAPSHOT версии

**Что такое SNAPSHOT:**
- Версия в активной разработке (например, `1.0-SNAPSHOT`)
- Артефакт может изменяться без смены номера версии
- Maven всегда проверяет обновления SNAPSHOT-зависимостей

**Отличие от релизной версии:**
| SNAPSHOT | Релиз |
|----------|-------|
| `1.0-SNAPSHOT` | `1.0` |
| Может меняться | Неизменяемый |
| Для разработки | Для продакшена |

---

## 7. Тег `<build>` — что внутри

**Обязателен ли тег:** Нет, не обязателен. Maven использует значения по умолчанию.

**Что может содержаться в `<build>`:**

```xml
<build>
    <!-- Директория исходников (по умолчанию src/main/java) -->
    <sourceDirectory>src/main/java</sourceDirectory>

    <!-- Директория ресурсов -->
    <resources>
        <resource>
            <directory>src/main/resources</directory>
        </resource>
    </resources>

    <!-- Директория результатов компиляции -->
    <outputDirectory>target/classes</outputDirectory>

    <!-- Имя итогового артефакта -->
    <finalName>my-app</finalName>

    <!-- Плагины -->
    <plugins>
        <plugin>...</plugin>
    </plugins>

    <!-- Управление плагинами -->
    <pluginManagement>...</pluginManagement>
</build>
```

---

## 8. Разница между плагинами и зависимостями

| Плагины (plugins) | Зависимости (dependencies) |
|-------------------|---------------------------|
| Выполняют задачи **во время сборки** | Используются **в коде приложения** |
| Компилируют, тестируют, упаковывают | Предоставляют библиотеки для импорта |
| Секция `<build><plugins>` | Секция `<dependencies>` |
| Не включаются в итоговый JAR | Включаются в classpath |

**Примеры из нашего проекта:**

**Зависимости (для написания кода):**
```xml
<!-- Логирование — используем в коде: Logger logger = LoggerFactory.getLogger(...) -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
</dependency>

<!-- JSON — используем в коде: objectMapper.writeValueAsString(...) -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

**Плагины (для процесса сборки):**
```xml
<!-- Запуск приложения — выполняет main-класс -->
<plugin>
    <artifactId>exec-maven-plugin</artifactId>
</plugin>

<!-- Анализ кода — проверяет на ошибки -->
<plugin>
    <artifactId>spotbugs-maven-plugin</artifactId>
</plugin>
```

---

## 9. Уровни логирования

**Уровни SLF4J (от высшего к низшему):**

| Уровень | Использование |
|---------|---------------|
| `ERROR` | Критические ошибки, приложение не может продолжать |
| `WARN` | Предупреждения, потенциальные проблемы |
| `INFO` | Информационные сообщения о ходе работы |
| `DEBUG` | Детальная информация для отладки |
| `TRACE` | Максимально подробная трассировка |

**Пример использования:**
```java
logger.error("Критическая ошибка: {}", e.getMessage());
logger.warn("Файл не найден, использую значение по умолчанию");
logger.info("Приложение запущено");
logger.debug("Итерация цикла: i = {}", i);
```

---

## 10. Разница между compile и exec:java

| `compile` | `exec:java` |
|-----------|-------------|
| **Фаза** жизненного цикла | **Цель (goal)** плагина |
| Компилирует .java → .class | Запускает main-метод |
| Часть стандартного цикла | Требует плагин exec-maven-plugin |
| `mvn compile` | `mvn exec:java` |

**Фаза vs Цель:**
- **Фаза (phase)** — этап жизненного цикла, выполняется последовательно
- **Цель (goal)** — конкретное действие плагина, формат `плагин:цель`

---

## 11. Репозитории Maven

**Три типа репозиториев:**

### Локальный репозиторий
- **Расположение:** `~/.m2/repository`
- **Назначение:** Кэш загруженных зависимостей
- **Когда используется:** Maven сначала ищет зависимости здесь
- **Пример пути:** `~/.m2/repository/org/slf4j/slf4j-api/2.0.9/slf4j-api-2.0.9.jar`

### Центральный репозиторий (Maven Central)
- **URL:** https://repo.maven.apache.org/maven2
- **Назначение:** Основной публичный репозиторий
- **Содержит:** Миллионы open-source библиотек
- **Когда используется:** Если зависимость не найдена локально

### Удалённый (корпоративный) репозиторий
- **Примеры:** Nexus, Artifactory, JFrog
- **Назначение:** Приватные библиотеки компании, прокси для Central
- **Настройка:** В `pom.xml` или `settings.xml`

**Порядок поиска зависимостей:**
```
1. Локальный (~/.m2/repository)
      ↓ не найдено
2. Удалённые (если настроены)
      ↓ не найдено
3. Maven Central
```

---

## 12. Зачем нужен pom.xml

**POM (Project Object Model)** — главный конфигурационный файл Maven.

**Зачем используется:**
1. **Идентификация проекта** — GAV координаты
2. **Управление зависимостями** — какие библиотеки подключить
3. **Конфигурация сборки** — как компилировать, упаковывать
4. **Настройка плагинов** — дополнительные действия при сборке
5. **Определение свойств** — версия Java, кодировка
6. **Наследование** — parent POM для общих настроек

---

## 13. После какой фазы создаются .class файлы

**Ответ:** После фазы `compile`

```
validate → compile → test → package → install
              ↓
         target/classes/*.class
```

**Команда для проверки:**
```bash
mvn compile
ls target/classes/org/example/
# Main.class  Person.class  JsonSerializer.class
```

---

## 14. Что такое компилятор для сборки

**Maven использует:** `maven-compiler-plugin`

**Что делает:**
- Вызывает `javac` (Java Compiler)
- Преобразует `.java` → `.class` (байт-код)
- Настраивается в `<properties>` или `<plugins>`

**Настройка версии Java:**
```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

**Или явно через плагин:**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <source>17</source>
        <target>17</target>
    </configuration>
</plugin>
```

**Дефолтный или указывается:**
- По умолчанию Maven использует `maven-compiler-plugin`
- Версия Java по умолчанию: 1.8 (если не указано иное)
- Рекомендуется явно указывать версию Java в `<properties>`

---

## 15. Демонстрация сборки через терминал

**Команды для демонстрации:**

```bash
# Перейти в директорию проекта
cd /path/to/project

# Очистка и полная сборка
mvn clean install

# Вывод покажет выполнение фаз:
# [INFO] --- clean ---
# [INFO] --- resources ---
# [INFO] --- compile ---
# [INFO] --- testResources ---
# [INFO] --- testCompile ---
# [INFO] --- test ---
# [INFO] --- package ---
# [INFO] --- install ---

# Запуск приложения
mvn exec:java
```

**Какие фазы выполнились при `mvn install`:**
1. `validate` — проверка проекта
2. `initialize` — инициализация
3. `generate-sources` — генерация исходников
4. `process-sources` — обработка исходников
5. `generate-resources` — генерация ресурсов
6. `process-resources` — копирование ресурсов в target
7. `compile` — компиляция исходного кода
8. `process-classes` — обработка классов
9. `generate-test-sources` — генерация тестовых исходников
10. `process-test-sources` — обработка тестовых исходников
11. `generate-test-resources` — генерация тестовых ресурсов
12. `process-test-resources` — копирование тестовых ресурсов
13. `test-compile` — компиляция тестов
14. `process-test-classes` — обработка тестовых классов
15. `test` — запуск тестов
16. `prepare-package` — подготовка к упаковке
17. `package` — создание JAR
18. `pre-integration-test` — подготовка интеграционных тестов
19. `integration-test` — интеграционные тесты
20. `post-integration-test` — завершение интеграционных тестов
21. `verify` — проверка пакета
22. `install` — установка в локальный репозиторий

---

## Краткая шпаргалка для защиты

```
GAV = GroupId + ArtifactId + Version (уникальный идентификатор)

Фазы: validate → compile → test → package → verify → install → deploy

clean удаляет: target/

compile создаёт: target/classes/*.class

package создаёт: target/имя-версия.jar

Плагины = для сборки (exec, spotbugs, compiler)
Зависимости = для кода (slf4j, jackson)

Репозитории: локальный (~/.m2) → удалённый → Central

SNAPSHOT = версия в разработке, может меняться
```
