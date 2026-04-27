package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.LikeStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final UserService userService;
    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final MpaService mpaService;
    private final LikeStorage likeStorage;

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

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
        validateMpa(film);
        validateGenres(film);
        validateReleaseDate(film);
        Film createdFilm = filmStorage.create(film);
        return findById(createdFilm.getId());
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            log.warn("Передан пустой ID {}", film.getId());
            throw new ValidationException("Id должен быть указан");
        }
        getFilmOrThrow(film.getId());
        validateMpa(film);
        validateGenres(film);
        validateReleaseDate(film);
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
        return getFilmOrThrow(filmId);
    }

    private void validateMpa(Film film) {
        if (film.getMpa() == null && film.getMpa().getId() == null) {
            throw new ValidationException("MPA должен быть указан");
        }

        mpaService.findById(film.getMpa().getId());

    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.warn("Ошибка валидации даты релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private void validateGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            // тесты подразумевают что может быть фильм без жанра
            // Genre -> Film get without genre
            return;
        }

        Set<Long> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        List<Genre> existingGenres = genreService.findAllByIds(genreIds);

        if (existingGenres.size() != genreIds.size()) {
            throw new NotFoundException("Один или несколько жанров не найдены");
        }

    }
}
