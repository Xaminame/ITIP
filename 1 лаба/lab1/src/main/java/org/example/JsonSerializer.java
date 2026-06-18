package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс для сериализации и десериализации объектов в JSON
 */
public class JsonSerializer {
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);
    private final ObjectMapper objectMapper;

    public JsonSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Сериализует объект в JSON строку
     * @param object объект для сериализации
     * @return JSON строка
     */
    public String toJson(Object object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            logger.info("Объект успешно сериализован в JSON: {}", json);
            return json;
        } catch (JsonProcessingException e) {
            logger.error("Ошибка сериализации объекта в JSON", e);
            return null;
        }
    }

    /**
     * Сериализует объект в форматированную JSON строку
     * @param object объект для сериализации
     * @return форматированная JSON строка
     */
    public String toJsonPretty(Object object) {
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            logger.info("Объект успешно сериализован в форматированный JSON");
            return json;
        } catch (JsonProcessingException e) {
            logger.error("Ошибка сериализации объекта в JSON", e);
            return null;
        }
    }

    /**
     * Десериализует JSON строку в объект указанного класса
     * @param json JSON строка
     * @param clazz класс объекта
     * @return десериализованный объект
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            T object = objectMapper.readValue(json, clazz);
            logger.info("JSON успешно десериализован в объект: {}", object);
            return object;
        } catch (JsonProcessingException e) {
            logger.error("Ошибка десериализации JSON в объект", e);
            return null;
        }
    }
}
