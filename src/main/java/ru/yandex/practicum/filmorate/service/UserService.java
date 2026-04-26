package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public UserService(
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("friendshipDbStorage") FriendshipStorage friendshipStorage) {
        this.friendshipStorage = friendshipStorage;
        this.userStorage = userStorage;
    }

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

    public boolean addFriend(Long userId, Long friendId) {

        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя самого себя добавить в друзья");
        }

        getUserOrThrow(userId);
        getUserOrThrow(friendId);
        boolean result;

        if (friendshipStorage.isFriend(friendId, userId)) {
            log.info("Подтверждена взаимная дружба {} с {}", friendId, userId);
            result = friendshipStorage.addFriend(userId, friendId, FriendshipStatus.CONFIRMED);
            friendshipStorage.updateFriendshipStatus(friendId, userId, FriendshipStatus.CONFIRMED);
        } else {
            log.info("Одностороняя дружба {} с {}", userId, friendId);
            result = friendshipStorage.addFriend(userId, friendId, FriendshipStatus.UNCONFIRMED);
        }

        return result;
    }

    public List<User> getFriends(Long userId) {
        getUserOrThrow(userId);
        return friendshipStorage.getFriends(userId);
    }

    public boolean removeFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя самого себя удалить из друзей");
        }

        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        return friendshipStorage.removeFriend(userId, friendId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {

        if (userId.equals(otherId)) {
            throw new ValidationException("Нельзя у самого себя искать общих друзей");
        }

        getUserOrThrow(userId);
        getUserOrThrow(otherId);

        return friendshipStorage.getCommonFriends(userId, otherId);
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

        boolean emailExists = userStorage.checkEmailDublication(user.getId(), user.getEmail());
        if (emailExists) {
            throw new ValidationException("Этот Email " + user.getEmail() + " уже используется");
        }
    }
}
