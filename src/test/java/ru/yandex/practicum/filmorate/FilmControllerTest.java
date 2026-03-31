package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;
    private UserController userController;

    @BeforeEach
    void setUp() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();

        UserService userService = new UserService(userStorage);
        FilmService filmService = new FilmService(filmStorage, userService);

        filmController = new FilmController(filmService);
        userController = new UserController(userService);
    }

    private Film createNewFilm() {
        Film film = new Film();
        film.setName("Valid film");
        film.setDescription("Description");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2005, 12, 28));
        return film;
    }

    private User createNewUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setLogin("rustam");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    // фильм до даты рождения
    @Test
    void shouldThrowExceptionWhenReleaseDateBeforeCinemaBirthday() {
        Film film = new Film();
        film.setName("Old film");
        film.setDescription("Description");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(ValidationException.class, () -> filmController.postFilm(film));
    }

    // фильм в дату рождения
    @Test
    void shouldCreateFilmWhenReleaseDateIsCinemaBirthday() {
        Film film = new Film();
        film.setName("Valid film");
        film.setDescription("Description");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Film createdFilm = filmController.postFilm(film);

        assertNotNull(createdFilm.getId());
        assertEquals("Valid film", createdFilm.getName());
    }

    // фильм с неизвестным ID
    @Test
    void shouldThrowExceptionWhenUpdatingUnknownFilm() {
        Film film = new Film();
        film.setId(666L);
        film.setName("Unknown Film");
        film.setDescription("desc");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        assertThrows(RuntimeException.class, () -> filmController.updateFilm(film));
    }

    @Test
    void shouldAddLike() {
        Film createdFilm = filmController.postFilm(createNewFilm());
        User createdUser = userController.postUser(createNewUser("rustam@yandex.ru"));

        filmController.addLike(createdFilm.getId(), createdUser.getId());
        assertEquals(1, createdFilm.getLikes().size());
    }

    @Test
    void shouldRemoveLike() {
        Film createdFilm = filmController.postFilm(createNewFilm());
        User createdUser = userController.postUser(createNewUser("rustam@yandex.ru"));
        User createdUser2 = userController.postUser(createNewUser("rustam2@yandex.ru"));

        filmController.addLike(createdFilm.getId(), createdUser.getId());
        filmController.addLike(createdFilm.getId(), createdUser2.getId());
        filmController.removeLike(createdFilm.getId(), createdUser.getId());
        assertEquals(1, createdFilm.getLikes().size());
        assertTrue(createdFilm.getLikes().contains(createdUser2.getId()));
    }

    @Test
    void shouldThrownExceptionWhenCountIsNegative() {
        filmController.postFilm(createNewFilm());
        assertThrows(IllegalArgumentException.class, () -> filmController.getPopularFilms(-1));
    }

    @Test
    void shouldThrownExceptionWhenCountIsZero() {
        filmController.postFilm(createNewFilm());
        assertThrows(IllegalArgumentException.class, () -> filmController.getPopularFilms(0));
    }

}


