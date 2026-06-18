# Методические указания по выполнению лабораторных работ по дисциплине «Информационные технологии и программирование»

для студентов направлений  
09.03.01 Информатика и вычислительная техника,  
09.03.04 Программная инженерия

---

## Лабораторная работа №1  
### «Инструмент сборки Maven»

**Цель**  
Ознакомиться с инструментом автоматической сборки проектов на Java — Maven. Создать проект на Maven согласно варианту, осуществить его запуск.

**Выполнение**

В удобной среде разработки выбираем «Создать новый проект», в настройках нового проекта стоит выбрать Build System «Maven» (рисунок 1.1), по желанию поменять GroupId и ArtefactId.

![](media/image1.png){width="4.766538713910761in" height="3.9583333333333335in"}

Рисунок 1.1 — Создание Maven проекта

Далее нажимаем «Create». После этого видим базовую структуру проекта, подобную изображенной на рисунке 1.2.

За сборку проекта при использовании Maven отвечает файл pom.xml, который обычно располагается в корне проекта. Что из себя представляет данный файл? Рассмотрим пример такого файла.

Информация для программного проекта, поддерживаемого Maven, содержится в XML-файле с именем **pom.xml** (от **Project Object Model**). При исполнении Maven проверяет прежде всего, содержит ли этот файл все необходимые данные и все ли данные синтаксически правильно записаны.

![](media/image2.png){width="6.6930555555555555in" height="2.604861111111111in"}  
Рисунок 1.2 — Созданный Maven проект и его структура

**Пример файла pom.xml**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <groupId>com.examclouds</groupId>
    <artifactId>courses</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.39</version>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>1.4.196</version>
        </dependency>
    </dependencies>

    <build>
        <sourceDirectory>../../src</sourceDirectory>
        <resources>
            <resource>
                <directory>resources</directory>
            </resource>
        </resources>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>2.0.2</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-checkstyle-plugin</artifactId>
                <version>2.17</version>
                <configuration>
                    <suppressionsLocation>suppressions.xml</suppressionsLocation>
                </configuration>
            </plugin>
        </plugins>
    </build>

    <reporting>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>2.0.2</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-checkstyle-plugin</artifactId>
                <version>2.17</version>
                <configuration>
                    <suppressionsLocation>suppressions.xml</suppressionsLocation>
                </configuration>
                <reportSets>
                    <reportSet>
                        <reports>
                            <report>checkstyle</report>
                        </reports>
                    </reportSet>
                </reportSets>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-pmd-plugin</artifactId>
                <version>3.8</version>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>findbugs-maven-plugin</artifactId>
                <version>3.0.4</version>
                <configuration>
                    <xmlOutput>true</xmlOutput>
                </configuration>
            </plugin>
        </plugins>
    </reporting>
</project>
```

### 1. Корневой элемент

Корневой элемент `<project>`, в котором прописана схема, облегчающая редактирование и проверку, и версия POM.

**Пример 2. Корневой элемент**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    ...
</project>
```

### 2. Заголовок

Внутри тега `project` содержится основная и обязательная информация о проекте:

**Пример 3. Заголовок**

```xml
<groupId>com.examclouds</groupId>
<artifactId>courses</artifactId>
<version>1.0-SNAPSHOT</version>
```

В *Maven* каждый проект идентифицируется парой *groupId, artifactId*.

Во избежание конфликта имён, **groupId** - наименование организации или подразделения и обычно действуют такие же правила, как и при именовании пакетов в Java - записывают доменное имя организации или сайта проекта.

**artifactId** - название проекта.

Внутри тега **version** хранится версия проекта.

Тройкой *groupId, artifactId, version* (далее - **GAV**) можно однозначно идентифицировать *jar* файл приложения или библиотеки. Если состояние кода для проекта не зафиксировано, то в конце к имени версии добавляется *"-SNAPSHOT"* что обозначает, что версия в разработке и результирующий *jar* файл может меняться.

### 3. Тег packaging

Тег `<packaging>` определяет какого типа файл будет создаваться как результат сборки. Возможные варианты *pom, jar, war, ear.*

Тег является необязательным. Если его нет, используется значение по умолчанию *- jar.*

### 4. Описание проекта

Также добавляется информация, которая не используется самим *Maven*, но нужна для программиста, чтобы понять, о чём этот проект:

**Пример 4. Описание проекта**

```xml
<name>powermock-core</name>                <!-- название проекта для человека -->
<description>PowerMock core functionality.</description>  <!-- описание проекта -->
<url>http://www.powermock.org</url>        <!-- сайт проекта -->
```

### 5. Зависимости

**Зависимости** - следующая очень важная часть *pom.xml* - тут хранится список всех библиотек (зависимостей), которые используются в проекте. Каждая библиотека идентифицируется так же как и сам проект — тройкой *groupId, artifactId, version* (GAV). Объявление зависимостей заключено в теге `<dependencies>...</dependencies>`.

Кроме GAV при описании зависимости может присутствовать тег `<scope>`. Он задаёт, для чего библиотека используется. В данном примере говорится, что библиотека с GAV *junit:junit:4.4* нужна только для выполнения тестов.

**Пример 5. Зависимости**

```xml
<dependencies>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.39</version>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>1.4.196</version>
    </dependency>
</dependencies>
```

### 6. Тег `<build>`

Тег `<build>` не обязательный, так как существуют значения по умолчанию. Этот раздел содержит информацию по самой сборке:

- где находятся исходные файлы,
- где находятся ресурсы,
- какие плагины используются.

**Пример 6. Тег `<build>`**

- `<sourceDirectory>` - определяет, откуда *Maven* будет брать файлы исходного кода. По умолчанию это *src/main/java*, но вы можете определить, где это вам удобно. Директория может быть только одна (без использования специальных плагинов).
- `<resources>` и вложенные в неё теги `<resource>` определяют одну или несколько директорий, где хранятся файлы ресурсов. Ресурсы в отличие от файлов исходного кода при сборке просто копируются. Директория по умолчанию *src/main/resources*.
- `<outputDirectory>` - определяет, в какую директорию компилятор будет сохранять результаты компиляции - `*.class` файлы. Значение по умолчанию - *target/classes*.
- `<finalName>` - имя результирующего *jar (war, ear ...)* файла с соответствующим типу расширением, который создаётся на фазе *package*. Значение по умолчанию — *artifactId-version*.

*Maven* плагины позволяют задать дополнительные действия, которые будут выполняться при сборке. Например, в приведённом примере добавлен плагин, который автоматически делает проверку кода на наличие "плохого" кода и потенциальных ошибок.

Это лишь пример некоторого готового файла pom.xml. Вернемся к нашему проекту. И добавим в него плагин для сборки, который поможет нам указать Main Class сразу из кода, чтоб не прописывать это при запуске. Итоговый файл pom.xml представлен на листинге 1.1.

**Листинг 1.1 — Файл pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>Test-Maven</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <mainClass>org.example.Main</mainClass>
                    <skip>false</skip>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

Далее нам нужно проделать запуск проекта. Это можно сделать как из среды разработки, так и из системной консоли. Для запуска сборки проекта на базе Maven используется команда mvn. В системах на базе Linux mvn можно установить, как sudo apt install maven, где apt — это ваш пакетный менеджер в Linux. Затем нам нужно из консоли переместиться в папку проекта (рисунок 1.3).

Чтоб создать команду запуска нам нужно определить фазы для запуска. Первой фазой будет clean, чтоб очистить директорию сборки, если проект уже собирался. Обычно clean используется для сборки «начистую». Вторая фаза будет install, в этой фазе компилируется исходный код, выполняются тесты и упаковываются результаты в jar или war файл, после чего упакованный файл копируется в локальный репозиторий Maven, что позволяет использовать его как зависимость в других проектах на этой же машине. Третья фаза exec:java, которая требуется для запуска приложения. Итоговая команда: mvn clean install exec:java. Запуск представлен на рисунке 1.4.

![](media/image3.png){width="4.989583333333333in" height="2.088888888888889in"}

Рисунок 1.3 — Перемещение в папку проекта

![](media/image4.png){width="5.1866896325459315in" height="4.208333333333333in"}

Рисунок 1.4 — Консольный запуск сборки

Теперь рассмотрим второй способ сборки. В среде разработки выбираем «Редактировать конфигурации сборки», в разных средах эта кнопка может отличаться. Затем нажимаем «Добавить новую конфигурацию сборки», «Maven». После чего видим окошко похожее на изображенное на рисунке 1.5.

![](media/image5.png){width="5.90625in" height="3.75625in"}  
Рисунок 1.5 — Окно создания новой конфигурации сборки

В этом окне надо в Command line занести наши фазы, а именно, *clean install exec:java* (рисунок 1.6). После чего нажимаем «Применить» и «Ок».

![](media/image6.png){width="5.511111111111111in" height="3.25in"}

Рисунок 1.6 — Готовая конфигурация Maven

После этого нажимаем на кнопку запуска конфигурации сборки в среде разработки. Результат запуска приведен на рисунке 1.7.

![](media/image7.png){width="6.6930555555555555in" height="1.5784722222222223in"}  
Рисунок 1.7 — Запуск конфигурации сборки

**Варианты заданий**

| № | Базовый класс | Дочерние классы |
|---|----------------|-----------------|
| 1 | Животные | Кошка, Попугай, Рыбка |
| 2 | Сотрудник | Администратор, Программист, Менеджер |
| 3 | Человек | Студент, Преподаватель, Ассистент преподавателя |
| 4 | Транспортное средство | Легковой автомобиль, Грузовой автомобиль, Мотоцикл |
| 5 | Велосипед | Горный велосипед, Детский велосипед, BMX |
| 6 | Геометрическая фигура | Шар, Параллелепипед, Цилиндр |
| 7 | Книга | Аудиокнига, Фильм, Мюзикл |
| 8 | Мебель | Стол, Стул, Кровать |
| 9 | Монстр | Гоблин, Русалка, Дракон |
|10 | Гаджет | Часы, Смартфон, Ноутбук |
|11 | Бытовая техника | Холодильник, Посудомоечная машина, Пылесос |
|12 | Приложение | Социальная сеть, Игра, Погода |
|13 | Оружие | Меч, Лук, Волшебная палочка |
|14 | Заведение | Кафе, Магазин, Библиотека |
|15 | Компьютерная периферия | Клавиатура, Наушники, Графический планшет |

**Контрольные вопросы**

1. Что такое Apache Maven и для чего он используется?
2. Как установить Maven на различные операционные системы?
3. Какова структура проекта Maven?
4. Что такое POM файл и какова его роль в проекте Maven?
5. Какова структура файла POM?
6. Что такое зависимости (dependencies) в Maven и как они определяются?
7. Что такое репозиторий Maven и какие виды репозиториев существуют?
8. Как добавить зависимость в проект Maven?
9. Что такое плагины в Maven и как они используются?
10. Как создать новый проект Maven с помощью команды Maven?
11. Что такое цели (goals) и фазы (phases) в Maven и в чем их отличие?
12. Как выполнить команду сборки проекта в Maven?
13. Что такое жизненный цикл сборки (build lifecycle) в Maven?
14. Как настроить профили (profiles) в Maven для разных сред (например, разработка и продакшн)?
15. Как управлять версиями зависимостей в Maven?
16. Что такое "SNAPSHOT" версии в Maven и как они используются?
17. Как использовать Maven для создания отчета о качестве кода?
18. Какие команды Maven используются для очистки проекта, сборки, тестирования и установки?
19. Как интегрировать Maven с системой контроля версий, такой как Git?
20. Как добавить и настроить сторонний репозиторий в проекте Maven?

---