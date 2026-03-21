package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    // фильм до даты рождения
    @Test
    void shouldThrowExceptionWhenReleaseDateBeforeCinemaBirthday() {
        FilmController controller = new FilmController();

        Film film = new Film();
        film.setName("Old film");
        film.setDescription("Description");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(ValidationException.class, () -> controller.postFilm(film));
    }

    // фильм в дату рождения
    @Test
    void shouldCreateFilmWhenReleaseDateIsCinemaBirthday() {
        FilmController controller = new FilmController();

        Film film = new Film();
        film.setName("Valid film");
        film.setDescription("Description");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Film createdFilm = controller.postFilm(film);

        assertNotNull(createdFilm.getId());
        assertEquals("Valid film", createdFilm.getName());
    }

    // фильм с неизвестным ID
    @Test
    void shouldThrowExceptionWhenUpdatingUnknownFilm() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setId(666L);
        film.setName("Unknown Film");
        film.setDescription("desc");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        assertThrows(RuntimeException.class, () -> controller.updateFilm(film));
    }

}


