package org.example.springlab3notifications.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String sayHello() {
        return "Привет, Spring Boot!";
    }

    @GetMapping("/goodbye")
    public String sayGoodbye() {
        return "До свидания, Spring Boot!";
    }

    @GetMapping("/greet")
    public String greet(@RequestParam String name) {
        return "Привет, " + name + "!";
    }

    @GetMapping("/user-info")
    public String userInfo(@RequestParam String name, @RequestParam int age) {
        return "Пользователь: " + name + ", возраст: " + age + " лет";
    }
}
