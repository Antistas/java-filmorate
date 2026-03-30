package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public void addFriend(Long userId, Long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public List<User> getFriends(Long userId) {
        return getUserOrThrow(userId).getFriends().stream().map(userStorage::findById).toList();
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = getUserOrThrow(userId);
        User otherUser = getUserOrThrow(otherId);

        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(userStorage::findById)
                .toList();
    }

    private User getUserOrThrow(Long userId) {
        User user = userStorage.findById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }
}
