# Шпаргалка для сдачи лабораторной работы №2

## Основные команды Gradle

| Команда | Что делает |
|---------|------------|
| `./gradlew clean build` | Полная сборка (очистка + компиляция + тесты) |
| `./gradlew run` | Запуск приложения |
| `./gradlew shadowJar` | Создание Fat JAR |
| `./gradlew printInfo` | Кастомная задача — информация о проекте |
| `./gradlew generateBuildPassport` | Кастомная задача — паспорт сборки |
| `./gradlew tasks --group=Custom` | Показать пользовательские задачи |
| `./gradlew dependencies` | Дерево зависимостей |

---

## Демонстрационный сценарий

```bash
# 1. Показать кастомные задачи
./gradlew tasks --group=Custom

# 2. Выполнить printInfo
./gradlew printInfo

# 3. Собрать проект
./gradlew clean build

# 4. Показать паспорт сборки
cat src/main/resources/build-passport.properties

# 5. Показать JAR-файлы
ls -la build/libs/

# 6. Запустить fat JAR
java -jar build/libs/lab2-1.0-SNAPSHOT-all.jar
```

---

## Ключевые понятия (краткие ответы)

| Понятие | Определение |
|---------|-------------|
| **Gradle** | Инструмент автоматизации сборки (компиляция, зависимости, тесты, упаковка) |
| **build.gradle.kts** | Конфигурация модуля: плагины, зависимости, задачи |
| **settings.gradle.kts** | Настройки проекта: имя, подключаемые модули |
| **implementation** | Область видимости зависимости (компиляция + runtime, не транзитивна) |
| **Fat JAR (uber-jar)** | JAR со всеми зависимостями внутри |
| **Shadow plugin** | Плагин для создания Fat JAR |
| **@TaskAction** | Аннотация, помечающая метод как точку входа задачи |
| **dependsOn** | Устанавливает зависимость между задачами |
| **SLF4J + Logback** | Стек логирования с уровнями (DEBUG, INFO, WARN, ERROR) |

---

## Что такое @TaskAction?

Аннотация `@TaskAction` помечает метод, который выполняется при запуске задачи.

```kotlin
abstract class PrintInfoTask : DefaultTask() {
    @TaskAction  // <-- точка входа
    fun print() {
        println("Проект: ${project.name}")
    }
}
```

Без неё Gradle не знает, какой метод вызывать.

### Как работает @TaskAction под капотом?

**1. Две фазы жизненного цикла Gradle:**

| Фаза | Что происходит | Когда выполняется код |
|------|----------------|----------------------|
| **Configuration** | Gradle читает `build.gradle.kts`, создаёт объекты задач | Код в теле класса и конструкторе |
| **Execution** | Gradle выполняет задачи | Код в методах с `@TaskAction` |

```kotlin
abstract class MyTask : DefaultTask() {
    init {
        // Выполняется на фазе CONFIGURATION (всегда!)
        println("Задача создана")
    }

    @TaskAction
    fun execute() {
        // Выполняется на фазе EXECUTION (только при запуске задачи)
        println("Задача выполняется")
    }
}
```

**2. Механизм обнаружения:**

Gradle использует рефлексию для поиска методов с аннотацией `@TaskAction`:
1. При регистрации задачи Gradle сканирует класс
2. Находит все методы, помеченные `@TaskAction`
3. Сохраняет их в список действий задачи (`task.actions`)
4. При выполнении вызывает все действия по порядку

**3. Несколько @TaskAction в одном классе:**

Можно иметь несколько методов — они выполнятся последовательно:

```kotlin
abstract class MultiActionTask : DefaultTask() {
    @TaskAction
    fun step1() {
        println("Шаг 1: подготовка")
    }

    @TaskAction
    fun step2() {
        println("Шаг 2: выполнение")
    }
}
```

⚠️ **Порядок выполнения не гарантирован!** Лучше использовать один метод.

**4. Альтернатива — doFirst / doLast:**

Вместо создания класса можно добавить действия напрямую:

```kotlin
tasks.register("hello") {
    doFirst {
        println("Начало")  // Выполняется первым
    }
    doLast {
        println("Конец")   // Выполняется последним
    }
}
```

Внутри `doFirst` и `doLast` добавляют действия в тот же список, что и `@TaskAction`.

**5. Почему задача без @TaskAction не делает ничего:**

```kotlin
abstract class BrokenTask : DefaultTask() {
    fun doSomething() {  // Нет @TaskAction!
        println("Этот код никогда не выполнится")
    }
}
```

Gradle создаст задачу, но её список действий будет пуст → при запуске ничего не произойдёт.

---

## Структура проекта

```
lab2/
├── build.gradle.kts          # Конфигурация сборки
├── settings.gradle.kts       # Настройки проекта
├── src/main/java/            # Исходный код
├── src/main/resources/       # Ресурсы (logback.xml, build-passport.properties)
├── src/test/                 # Тесты
└── build/libs/               # Собранные JAR-файлы
    ├── lab2-1.0-SNAPSHOT.jar
    └── lab2-1.0-SNAPSHOT-all.jar  # Fat JAR
```

---

## Зависимости проекта

- **Apache Commons Lang3** — утилиты для работы со строками (`StringUtils`)
- **SLF4J + Logback** — логирование

---

## Дерево зависимостей (`./gradlew dependencies`)

**Дерево зависимостей** — визуальное представление всех библиотек проекта, включая **транзитивные зависимости** (зависимости зависимостей).

### Пример вывода для этого проекта:

```
runtimeClasspath
+--- org.apache.commons:commons-lang3:3.14.0      ← прямая зависимость
+--- ch.qos.logback:logback-classic:1.4.14        ← прямая зависимость
|    +--- ch.qos.logback:logback-core:1.4.14      ← транзитивная
|    \--- org.slf4j:slf4j-api:2.0.7 -> 2.0.9      ← конфликт версий разрешён
\--- org.slf4j:slf4j-api:2.0.9                    ← прямая зависимость
```

### Что означают символы:

| Символ | Значение |
|--------|----------|
| `+---` | Зависимость (не последняя в списке) |
| `\---` | Последняя зависимость на уровне |
| `\|` | Вложенность (транзитивная зависимость) |
| `->` | Конфликт версий — Gradle выбрал более новую |

### Что такое транзитивная зависимость?

Библиотека, которую вы **не указывали явно**, но она нужна вашей зависимости.

Пример: вы подключили `logback-classic`, а он требует `logback-core` — Gradle скачает обе.

### Зачем смотреть дерево зависимостей?

1. **Понять**, какие библиотеки реально попадают в сборку
2. **Найти конфликты** версий (стрелка `->`)
3. **Отследить**, откуда взялась неожиданная зависимость
4. **Оптимизировать** размер JAR, исключив лишнее

---

## Пользовательские задачи

### PrintInfoTask
Выводит название проекта и версию Gradle.

### GenerateBuildPassportTask
Генерирует `build-passport.properties` с информацией:
- Имя пользователя
- ОС
- Версия Java
- Время сборки

Интегрирована в сборку через `dependsOn(tasks.named("generateBuildPassport"))`.

---

## Что такое паспорт сборки (Build Passport)?

**Паспорт сборки** — это файл с метаданными о процессе сборки приложения, который упаковывается вместе с программой.

### Зачем нужен паспорт сборки?

1. **Трассировка** — позволяет узнать, кто, когда и в каком окружении собрал конкретный артефакт
2. **Отладка** — помогает воспроизвести проблемы (например, баг проявляется только на определённой версии Java)
3. **Аудит** — в корпоративных средах важно знать происхождение каждой сборки

### Что делает команда `./gradlew generateBuildPassport`?

Создаёт файл `src/main/resources/build-passport.properties` со следующим содержимым:

```properties
# Build Passport
build.user=dimarik              # Кто собрал
build.os=Mac OS X               # На какой ОС
build.java.version=17.0.1       # Версия Java
build.timestamp=2024-01-15T14:30:00  # Когда собрано
build.greeting=Добро пожаловать в приложение lab2!
```

### Как это работает в коде?

```kotlin
abstract class GenerateBuildPassportTask : DefaultTask() {
    @get:OutputFile
    val outputFile: RegularFileProperty = project.objects.fileProperty()

    @TaskAction
    fun generate() {
        // Собираем информацию об окружении
        val userName = System.getenv("USER") ?: "unknown"
        val osName = System.getProperty("os.name")
        val javaVersion = System.getProperty("java.version")
        val buildTime = LocalDateTime.now().toString()

        // Записываем в файл
        outputFile.get().asFile.writeText(content)
    }
}
```

### Интеграция в процесс сборки

Задача автоматически выполняется при сборке благодаря зависимости:

```kotlin
tasks.named("processResources") {
    dependsOn(tasks.named("generateBuildPassport"))
}
```

Это означает: перед обработкой ресурсов (копированием в JAR) — сгенерируй паспорт.
