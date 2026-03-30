package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Запрошен список пользователей");
        return userStorage.findAll();
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        fillNameIsBlank(user);
        checkEmailDuplication(user);
        User createdUser = userStorage.create(user);
        log.info("Создан пользователь {}", createdUser);
        return createdUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя: {}", user);
        if (user.getId() == null) {
            log.warn("Передан пустой id = {}", user.getId());
            throw new ValidationException("Id должен быть указан");
        }
        if (userStorage.findById(user.getId()) == null) {
            log.warn("Пользователь не найден, id = {}", user.getId());
            throw new NotFoundException("Пользователь не найден, id = " + user.getId());
        }
        checkEmailDuplication(user);
        fillNameIsBlank(user);
        User updatedUser = userStorage.update(user);
        log.info("Обновлён пользователь {}", updatedUser);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос на добавление друга {} от {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос на удаление друга {} у {}", friendId, id);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        log.info("Получен запрос на вывод друзей {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен запрос на поиск общих друзей {} у {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    private void fillNameIsBlank(User user) {
        if (user.getName() == null || user.getName().trim().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void checkEmailDuplication(User user) {
        boolean emailExists = userStorage.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail())
                        && (user.getId() == null || !u.getId().equals(user.getId())));

        if (emailExists) {
            throw new ValidationException("Этот Email уже используется");
        }
    }
}
