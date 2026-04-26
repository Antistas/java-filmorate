package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    private Film createFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        film.setMpa(createMpa(1));
        film.setGenres(Set.of(createGenre(1), createGenre(2)));
        return film;
    }

    private MpaRating createMpa(Integer id) {
        MpaRating mpa = new MpaRating();
        mpa.setId(id.longValue());
        return mpa;
    }

    private Genre createGenre(Integer id) {
        Genre genre = new Genre();
        genre.setId(id.longValue());
        return genre;
    }

    @Test
    void shouldFindFilmById() {
        Optional<Film> filmOptional = filmStorage.findById(1L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getId()).isEqualTo(1L);
                    assertThat(film.getName()).isEqualTo("Матрица");
                    assertThat(film.getReleaseDate()).isEqualTo(LocalDate.of(1999, 3, 31));
                    assertThat(film.getDuration()).isEqualTo(136);
                });
    }

    @Test
    void shouldReturnEmptyOptionalWhenFilmNotFound() {
        assertThat(filmStorage.findById(999999L)).isEmpty();
    }

    @Test
    void shouldCreateFilm() {
        Film createdFilm = filmStorage.create(createFilm("Created film"));
        assertThat(createdFilm.getId()).isNotNull();
        assertThat(filmStorage.findById(createdFilm.getId()))
                .isPresent()
                .hasValueSatisfying(savedFilm -> {
                    assertThat(savedFilm.getName()).isEqualTo("Created film");
                    assertThat(savedFilm.getDescription()).isEqualTo("Test description");
                    assertThat(savedFilm.getReleaseDate()).isEqualTo(LocalDate.of(2000, 1, 1));
                    assertThat(savedFilm.getDuration()).isEqualTo(100);
                    assertThat(savedFilm.getMpa().getId()).isEqualTo(1);
                });
    }

    @Test
    void shouldSaveFilmGenresWhenCreateFilm() {
        Film createdFilm = filmStorage.create(createFilm("Created film"));
        assertThat(createdFilm.getGenres().size()).isEqualTo(2);
    }

    @Test
    void shouldUpdateFilm() {
        Film createdFilm = filmStorage.create(createFilm("Before update"));
        createdFilm.setName("After update");
        createdFilm.setDescription("Updated description");
        createdFilm.setReleaseDate(LocalDate.of(2010, 5, 5));
        createdFilm.setDuration(150);
        createdFilm.setMpa(createMpa(3));
        filmStorage.update(createdFilm);
        assertThat(filmStorage.findById(createdFilm.getId()))
                .isPresent()
                .hasValueSatisfying(savedFilm -> {
                    assertThat(savedFilm.getName()).isEqualTo("After update");
                    assertThat(savedFilm.getDescription()).isEqualTo("Updated description");
                    assertThat(savedFilm.getReleaseDate()).isEqualTo(LocalDate.of(2010, 5, 5));
                    assertThat(savedFilm.getDuration()).isEqualTo(150);
                    assertThat(savedFilm.getMpa().getId()).isEqualTo(3);
                });
    }

    @Test
    void shouldFindAllFilms() {
        int sizeBefore = filmStorage.findAll().size();
        Film createdFilm = filmStorage.create(createFilm("Find all film"));
        List<Film> films = filmStorage.findAll();
        assertThat(films).hasSize(sizeBefore + 1);
        assertThat(films)
                .extracting(Film::getId)
                .contains(createdFilm.getId());
    }

    @Test
    void shouldReturnPopularFilmsSortedByLikes() {
        List<Film> popularFilms = filmStorage.getPopularFilms(3);
        assertThat(popularFilms).hasSize(3);
        assertThat(popularFilms)
                .extracting(Film::getId)
                .containsExactly(1L, 2L, 3L);
    }

    @Test
    void shouldLimitPopularFilmsCount() {
        List<Film> popularFilms = filmStorage.getPopularFilms(2);
        assertThat(popularFilms).hasSize(2);
    }

    @Test
    void shouldIncludeFilmsWithoutLikesInPopularFilms() {
        Film filmWithoutLikes = filmStorage.create(createFilm("Film without likes"));
        List<Film> popularFilms = filmStorage.getPopularFilms(100);
        assertThat(popularFilms)
                .extracting(Film::getId)
                .contains(filmWithoutLikes.getId());
    }
}