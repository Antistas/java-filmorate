package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {
    boolean addFriend(Long id1, Long id2, FriendshipStatus status);

    List<User> getFriends(Long userId);

    void updateFriendshipStatus(Long id1, Long id2, FriendshipStatus status);

    boolean isFriend(Long userId, Long friendId);

    boolean removeFriend(Long userId, Long friendId);

    List<User> getCommonFriends(Long userId, Long otherId);
}
