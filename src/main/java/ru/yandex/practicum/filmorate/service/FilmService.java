package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.LikeStorage;

import java.util.*;

@Slf4j
@Service
public class FilmService {

    private final UserService userService;
    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final MpaService mpaService;
    private final LikeStorage likeStorage;

    public FilmService(
            UserService userService,
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            GenreService genreService,
            MpaService mpaService,
            LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.genreService = genreService;
        this.mpaService = mpaService;
        this.likeStorage = likeStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        Film createdFilm = filmStorage.create(film);
        return findById(createdFilm.getId());
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            log.warn("Передан пустой ID {}", film.getId());
            throw new ValidationException("Id должен быть указан");
        }

        getFilmOrThrow(film.getId());
        return filmStorage.update(film);
    }

    public void addLike(Long filmId, Long userId) {
        getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        likeStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        likeStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Количество фильмов должно быть больше нуля");
        }

        return filmStorage.getPopularFilms(count);
    }

    private Film getFilmOrThrow(Long filmId) {
        Optional<Film> film = filmStorage.findById(filmId);
        if (film.isEmpty()) {
            throw new NotFoundException("Фильм не найден, id = " + filmId);
        }
        return film.get();
    }

    // приходится дублировать, так как мы обращаемся только storage
    private User getUserOrThrow(Long userId) {
        return userService.findById(userId);
    }

    public Film findById(Long filmId) {
        Film film = getFilmOrThrow(filmId);
        film.setGenres(new LinkedHashSet<>(genreService.getGenresByFilmId(filmId)));
        if (film.getMpa() != null) {
            MpaRating fullMpa = mpaService.findById(film.getMpa().getId());
            film.setMpa(fullMpa);
        }
        return film;
    }
}
