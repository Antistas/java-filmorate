package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Запрошен список пользователей. Количество: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        validate(user);
        fillNameIsBlank(user);
        checkEmailDuplication(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан пользователь {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {

        validate(user);
        fillNameIsBlank(user);

        if (user.getId() == null) {
            log.warn("Передан пустой id = {}", user.getId());
            throw new ValidationException("Id должен быть указан");
        }

        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь не найден id = {}", user.getId());
            throw new NotFoundException("Пользователь не найден");
        }

        if (users.values().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) && !u.getId().equals(user.getId()))) {
            log.warn("Ошибка валидации Email на дубликаты: {}", user.getEmail());
            throw new ValidationException("Этот Email уже используется");
        }

        users.put(user.getId(), user);
        log.info("Обновлён пользователь {}", user);
        return user;
    }

    private void fillNameIsBlank(User user) {
        if (user.getName() == null || user.getName().trim().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void checkEmailDuplication(User user) {
        if (users.values().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            log.warn("Ошибка валидации Email на дубликаты: {}", user.getEmail());
            throw new ValidationException("Этот имейл уже используется");
        }
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации email: {}", user.getEmail());
            throw new ValidationException("Email некорректен");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации login: {}", user.getLogin());
            throw new ValidationException("Login некорректен");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации birthday: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
