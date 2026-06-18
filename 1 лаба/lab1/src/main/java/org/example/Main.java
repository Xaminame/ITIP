package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Главный класс приложения
 * Демонстрирует использование логирования и работу с JSON
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("=== Запуск приложения ===");

        // Демонстрация логирования
        logger.info("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            logger.debug("Итерация цикла: i = {}", i);
        }

        // Демонстрация работы с JSON
        logger.info("=== Демонстрация работы с JSON ===");

        JsonSerializer serializer = new JsonSerializer();

        // Создаем объект Person
        Person person = new Person("Иван Иванов", 25, "ivan@example.com");
        logger.info("Создан объект: {}", person);

        // Сериализация в JSON
        String json = serializer.toJsonPretty(person);
        logger.info("Сериализованный JSON:\n{}", json);

        // Десериализация из JSON
        String inputJson = "{\"name\":\"Петр Петров\",\"age\":30,\"email\":\"petr@example.com\"}";
        logger.info("Входной JSON для десериализации: {}", inputJson);

        Person deserializedPerson = serializer.fromJson(inputJson, Person.class);
        logger.info("Десериализованный объект: {}", deserializedPerson);

        logger.info("=== Приложение завершено ===");
    }
}
