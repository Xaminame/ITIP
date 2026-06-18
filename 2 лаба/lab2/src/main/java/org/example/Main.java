package org.example;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("=== Начало работы программы ===");

        readBuildPassport();

        Scanner scanner = new Scanner(System.in);

        System.out.println("\nДобро пожаловать! Введите строку для обработки:");
        String input = scanner.nextLine();

        logger.info("Пользователь ввел строку: {}", input);

        String reversed = StringUtils.reverse(input);
        String capitalized = StringUtils.capitalize(input);
        String upperCase = StringUtils.upperCase(input);
        String swappedCase = StringUtils.swapCase(input);

        System.out.println("\n=== Результаты обработки ===");
        logger.info("Исходная строка: {}", input);
        logger.info("Реверс строки: {}", reversed);
        logger.info("С заглавной буквы: {}", capitalized);
        logger.info("В верхнем регистре: {}", upperCase);
        logger.info("С инверсией регистра: {}", swappedCase);

        boolean isBlank = StringUtils.isBlank(input);
        boolean isNumeric = StringUtils.isNumeric(input);
        boolean isAlpha = StringUtils.isAlpha(input);

        logger.info("Строка пустая/пробелы: {}", isBlank);
        logger.info("Строка числовая: {}", isNumeric);
        logger.info("Строка буквенная: {}", isAlpha);

        scanner.close();
        logger.info("=== Завершение работы программы ===");
    }

    private static void readBuildPassport() {
        logger.info("Чтение паспорта сборки...");

        try (InputStream input = Main.class.getClassLoader()
                .getResourceAsStream("build-passport.properties")) {

            if (input == null) {
                logger.warn("Файл build-passport.properties не найден");
                return;
            }

            Properties props = new Properties();
            props.load(input);

            System.out.println("\n=== Паспорт сборки ===");
            logger.info("Пользователь сборки: {}", props.getProperty("build.user"));
            logger.info("Операционная система: {}", props.getProperty("build.os"));
            logger.info("Версия Java: {}", props.getProperty("build.java.version"));
            logger.info("Время сборки: {}", props.getProperty("build.timestamp"));
            logger.info("Приветствие: {}", props.getProperty("build.greeting"));

        } catch (IOException e) {
            logger.error("Ошибка чтения паспорта сборки: {}", e.getMessage());
        }
    }
}
