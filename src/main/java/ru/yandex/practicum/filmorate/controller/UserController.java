package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

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

        fillNameIsBlank(user);
        checkEmailDuplication(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан пользователь {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {

        checkEmailDuplication(user);
        fillNameIsBlank(user);

        if (user.getId() == null) {
            log.warn("Передан пустой id = {}", user.getId());
            throw new ValidationException("Id должен быть указан");
        }

        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь не найден, id = {}", user.getId());
            throw new NotFoundException("Пользователь не найден, id = " + user.getId());
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
        boolean emailExists = users.values().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail())
                        && (user.getId() == null || !u.getId().equals(user.getId())));

        if (emailExists) {
            throw new ValidationException("Этот Email уже используется");
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
