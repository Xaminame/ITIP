# Команды для демонстрации лабораторной работы №2

## Сборка проекта

### Полная сборка (очистка + компиляция + тесты)
```bash
./gradlew clean build
```
Выполняет очистку директории `build/`, компиляцию исходного кода, запуск тестов и упаковку в JAR.

### Только компиляция
```bash
./gradlew compileJava
```
Компилирует исходный код без запуска тестов.

---

## Запуск приложения

### Запуск через Gradle
```bash
./gradlew run
```
Запускает приложение с использованием плагина `application`. Требует интерактивного ввода.

### Запуск fat JAR
```bash
java -jar build/libs/lab2-1.0-SNAPSHOT-all.jar
```
Запускает собранный fat JAR со всеми зависимостями. Предварительно необходимо выполнить сборку.

---

## Пользовательские задачи

### Вывод информации о проекте
```bash
./gradlew printInfo
```
Выводит:
- Название проекта
- Версию Gradle

**Пример вывода:**
```
=============================
Это моя первая пользовательская задача!
Проект: lab2
Версия Gradle: 8.5
=============================
```

### Генерация паспорта сборки
```bash
./gradlew generateBuildPassport
```
Создает файл `src/main/resources/build-passport.properties` с информацией:
- Имя пользователя
- Операционная система
- Версия Java
- Время сборки
- Приветственное сообщение

---

## Плагин Shadow (fat JAR)

### Создание fat JAR
```bash
./gradlew shadowJar
```
Создает JAR-файл со всеми зависимостями: `build/libs/lab2-1.0-SNAPSHOT-all.jar`

### Проверка созданных JAR-файлов
```bash
ls -la build/libs/
```
Должно быть два файла:
- `lab2-1.0-SNAPSHOT.jar` — обычный JAR
- `lab2-1.0-SNAPSHOT-all.jar` — fat JAR со всеми зависимостями

---

## Просмотр задач Gradle

### Все задачи
```bash
./gradlew tasks
```

### Только пользовательские задачи (группа Custom)
```bash
./gradlew tasks --group=Custom
```

### Подробная информация о задаче
```bash
./gradlew help --task printInfo
./gradlew help --task generateBuildPassport
```

---

## Полезные команды

### Очистка проекта
```bash
./gradlew clean
```
Удаляет директорию `build/`.

### Проверка зависимостей
```bash
./gradlew dependencies
```
Выводит дерево зависимостей проекта.

### Обновление Gradle Wrapper
```bash
./gradlew wrapper --gradle-version=8.5
```

---

## Демонстрационный сценарий

1. **Показать структуру проекта:**
   ```bash
   ls -la
   cat build.gradle.kts
   ```

2. **Показать пользовательские задачи:**
   ```bash
   ./gradlew tasks --group=Custom
   ```

3. **Выполнить задачу printInfo:**
   ```bash
   ./gradlew printInfo
   ```

4. **Собрать проект:**
   ```bash
   ./gradlew clean build
   ```

5. **Показать сгенерированный паспорт сборки:**
   ```bash
   cat src/main/resources/build-passport.properties
   ```

6. **Показать созданные JAR-файлы:**
   ```bash
   ls -la build/libs/
   ```

7. **Запустить fat JAR:**
   ```bash
   java -jar build/libs/lab2-1.0-SNAPSHOT-all.jar
   ```
   Ввести тестовую строку и показать результат обработки.

---

## Примечания

- Используйте `./gradlew` вместо `gradle` для гарантированной совместимости версий
- Fat JAR включает все зависимости и может запускаться на любой машине с установленной Java
- Паспорт сборки автоматически генерируется при каждой сборке (интегрирован с `processResources`)
