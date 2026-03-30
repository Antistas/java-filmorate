package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(Long filmId, Long userId) {
        Film film = getFilmOrThrow(filmId);
        User user = getUserOrThrow(userId);
        film.getLikes().add(user.getId());
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmOrThrow(filmId);
        User user = getUserOrThrow(userId);
        film.getLikes().remove(user.getId());
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Количество фильмов должно быть больше нуля");
        }

        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    private Film getFilmOrThrow(Long filmId) {
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм не найден, id = " + filmId);
        }
        return film;
    }

    // приходится дублировать, так как мы обращаемся только storage
    private User getUserOrThrow(Long userId) {
        User user = userStorage.findById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден, id = " + userId);
        }
        return user;
    }
}
