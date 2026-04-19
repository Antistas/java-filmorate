package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        if (user.getId() == null) {
            log.warn("Передан пустой id = {}", user.getId());
            throw new ValidationException("Id должен быть указан");
        }
        getUserOrThrow(user.getId());
        checkEmailDuplication(user);
        return userStorage.update(user);
    }

    public User findById(Long id) {
        return getUserOrThrow(id);
    }

    public void addFriend(Long userId, Long friendId) {

        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя самого себя добавить в друзья");
        }

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        if (friend.getFriends().containsKey(userId)) {
            user.getFriends().put(friendId, FriendshipStatus.CONFIRMED);
            friend.getFriends().put(userId, FriendshipStatus.CONFIRMED);
        } else {
            user.getFriends().put(friendId, FriendshipStatus.UNCONFIRMED);
        }
    }

    public List<User> getFriends(Long userId) {
        return getUserOrThrow(userId).getFriends().entrySet()
                .stream()
                .filter(entry -> entry.getValue() == FriendshipStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .map(id -> userStorage.findById(id)
                        .orElseThrow(() -> new NotFoundException("Пользователь не найден, id = " + id)))
                .toList();
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя самого себя удалить из друзей");
        }

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {

        if (userId.equals(otherId)) {
            throw new ValidationException("Нельзя у самого себя искать общих друзей");
        }

        User user = getUserOrThrow(userId);
        User otherUser = getUserOrThrow(otherId);

        return user.getFriends().entrySet()
                .stream()
                .filter(entry -> entry.getValue() == FriendshipStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .filter(friendId -> otherUser.getFriends().get(friendId) == FriendshipStatus.CONFIRMED)
                .map(id -> userStorage.findById(id)
                        .orElseThrow(() -> new NotFoundException("Пользователь не найден, id = " + id)))
                .toList();
    }

    private User getUserOrThrow(Long userId) {
        Optional<User> user = userStorage.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь c id = " + userId + " не найден");
        }
        return user.get();
    }

    public void fillNameIsBlank(User user) {
        if (user.getName() == null || user.getName().trim().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public void checkEmailDuplication(User user) {
        boolean emailExists = userStorage.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail())
                        && (user.getId() == null || !u.getId().equals(user.getId())));

        if (emailExists) {
            throw new ValidationException("Этот Email " + user.getEmail() + " уже используется");
        }
    }
}
