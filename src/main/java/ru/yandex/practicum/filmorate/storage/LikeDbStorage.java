package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.interfaces.LikeStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
@Qualifier("LikeDbStorage")
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, filmId);
            stmt.setLong(2, userId);
            return stmt;
        }, keyHolder);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = """
            DELETE FROM film_like
            WHERE user_id = ? AND film_id = ?
            """;

        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public boolean isLiked(Long filmId, Long userId) {
        String sql = """
            SELECT COUNT(*)
            FROM film_like
            WHERE user_id = ? AND film_id = ?
            """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, filmId);
        return count != null && count > 0;
    }
}
