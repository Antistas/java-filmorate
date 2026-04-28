package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Component
@Qualifier("friendshipDbStorage")
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    @Override
    public boolean addFriend(Long userId, Long friendId, FriendshipStatus status) {
        String sql = "INSERT INTO friendship (user_id, friend_id, confirmed) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int affectedRows = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, userId);
            stmt.setLong(2, friendId);
            stmt.setBoolean(3, status == FriendshipStatus.CONFIRMED);
            return stmt;
        }, keyHolder);

        return affectedRows > 0;
    }

    @Override
    public List<User> getFriends(Long userId) {
        String sql = """
            SELECT u.*
            FROM users u
            JOIN friendship f ON u.id = f.friend_id
            WHERE f.user_id = ?
            ORDER BY u.id
            """;

        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    @Override
    public void updateFriendshipStatus(Long userId, Long friendId, FriendshipStatus status) {
        String sql = "UPDATE friendship SET confirmed = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, status == FriendshipStatus.CONFIRMED, userId, friendId);
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        String sql = """
            SELECT COUNT(*)
            FROM friendship
            WHERE user_id = ? AND friend_id = ?
            """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId);
        return count != null && count > 0;
    }

    @Override
    public boolean removeFriend(Long userId, Long friendId) {
        String sql = """
            DELETE FROM friendship
            WHERE user_id = ? AND friend_id = ?
            """;

        int affectedRows = jdbcTemplate.update(sql, userId, friendId);
        return affectedRows > 0;
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        String sql = """
            SELECT u.*
            FROM users u
            JOIN friendship f1 ON u.id = f1.friend_id
            JOIN friendship f2 ON u.id = f2.friend_id
            WHERE f1.user_id = ?
              AND f2.user_id = ?
            ORDER BY u.id
            """;

        return jdbcTemplate.query(sql, userRowMapper, userId, otherId);
    }
}
