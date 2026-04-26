package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    @Override
    public Genre create(Genre genre) {
        String sql = "INSERT INTO genres (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, genre.getName());
            return stmt;
        }, keyHolder);

        genre.setId(keyHolder.getKey().longValue());
        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        String sql = "UPDATE genres SET name = ? WHERE id = ?";

        jdbcTemplate.update(sql,
                genre.getName(),
                genre.getId()
        );

        return genre;
    }

    @Override
    public Collection<Genre> findAll() {
        return jdbcTemplate.query("SELECT * FROM genres", genreRowMapper);
    }

    @Override
    public Optional<Genre> findById(Long id) {
        List<Genre> genres = jdbcTemplate.query("SELECT * FROM genres WHERE id = ?", genreRowMapper, id);
        return genres.stream().findFirst();
    }

    public Collection<Genre> findByFilmId(Long id) {
        return jdbcTemplate.query(
                "SELECT g.id, g.name FROM genres g INNER JOIN film_genre fg ON fg.genre_id = g.id " +
                        "WHERE fg.film_id = ?", genreRowMapper, id);
    }
}
