package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Component
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());

            if (film.getMpa() != null) {
                stmt.setLong(5, film.getMpa().getId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        saveFilmGenres(film);
        return film;
    }

    private void saveFilmGenres(Film film) {
        if (film.getGenres() == null) {
            return;
        }
        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql, film.getGenres(), film.getGenres().size(),
                (ps, genre) -> {
                    ps.setLong(1, film.getId());
                    ps.setLong(2, genre.getId());
                });
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());

        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        saveFilmGenres(film);
        return film;
    }

    @Override
    public List<Film> findAll() {
        String sql = """
            SELECT f.id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.duration,
                   m.id AS mpa_id,
                   m.name AS mpa_name,
                   m.comment AS mpa_comment
            FROM films f
            JOIN mpa_rating m ON f.mpa_id = m.id
            ORDER BY f.id
            """;

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        fillGenres(films);
        return films;
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sql = """
            SELECT f.id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.duration,
                   m.id AS mpa_id,
                   m.name AS mpa_name,
                   m.comment AS mpa_comment
            FROM films f
            JOIN mpa_rating m ON f.mpa_id = m.id
            WHERE f.id = ?
            """;

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, id);

        return films.stream()
                .findFirst()
                .map(this::fillGenres);
    }

    private Film fillGenres(Film film) {
        String sql = """
            SELECT g.id, g.name
            FROM genres g
            JOIN film_genre fg ON g.id = fg.genre_id
            WHERE fg.film_id = ?
            ORDER BY g.id
            """;

        Set<Genre> genres = new LinkedHashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, film.getId()));

        film.setGenres(genres);
        return film;
    }

    private void fillGenres(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }

        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();

        String placeholders = filmIds.stream()
                .map(id -> "?")
                .collect(java.util.stream.Collectors.joining(", "));

        String sql = """
            SELECT fg.film_id, g.id, g.name
            FROM film_genre fg
            JOIN genres g ON g.id = fg.genre_id
            WHERE fg.film_id IN (%s)
            ORDER BY fg.film_id, g.id
            """.formatted(placeholders);

        Map<Long, Set<Genre>> genresByFilmId = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            Genre genre = new Genre();
            genre.setId(rs.getLong("id"));
            genre.setName(rs.getString("name"));
            genresByFilmId.computeIfAbsent(filmId, id -> new LinkedHashSet<>()).add(genre);
        }, filmIds.toArray());

        films.forEach(film ->
                film.setGenres(genresByFilmId.getOrDefault(film.getId(), new LinkedHashSet<>()))
        );
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = """
            SELECT f.id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.duration,
                   m.id AS mpa_id,
                   m.name AS mpa_name,
                   m.comment AS mpa_comment
            FROM films f
            JOIN mpa_rating m ON f.mpa_id = m.id
            LEFT JOIN film_like fl ON fl.film_id = f.id
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, m.id, m.name, m.comment
            ORDER BY COUNT(fl.user_id) DESC, f.id ASC
            LIMIT ?
            """;

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, count);
        fillGenres(films);
        return films;
    }
}
