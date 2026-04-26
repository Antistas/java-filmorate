package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.storage.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FriendshipDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendshipDbStorageTest {

    private final FriendshipDbStorage friendshipStorage;
    private final UserDbStorage userStorage;

    @Test
    void shouldAddFriend() {
        friendshipStorage.addFriend(1L, 5L, FriendshipStatus.UNCONFIRMED);
        assertThat(friendshipStorage.isFriend(1L, 5L)).isTrue();
    }

    @Test
    void shouldRemoveFriend() {
        friendshipStorage.addFriend(1L, 5L, FriendshipStatus.UNCONFIRMED);
        boolean removed = friendshipStorage.removeFriend(1L, 5L);
        assertThat(removed).isTrue();
        assertThat(friendshipStorage.isFriend(1L, 5L)).isFalse();
    }

    @Test
    void shouldReturnFriends() {
        assertThat(friendshipStorage.getFriends(1L))
                .extracting("id")
                .contains(2L);
    }

    @Test
    void shouldReturnCommonFriends() {
        assertThat(friendshipStorage.getCommonFriends(1L, 3L))
                .extracting("id")
                .contains(2L);
    }
}
