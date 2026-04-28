package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {

    private final GenreDbStorage genreStorage;

    @Test
    void shouldFindAllGenres() {
        assertThat(genreStorage.findAll().size()).isNotZero();
    }

    @Test
    void shouldFindGenreById() {
        assertThat(genreStorage.findById(1L))
                .isPresent()
                .hasValueSatisfying(genre -> {
                    assertThat(genre.getId()).isEqualTo(1L);
                    assertThat(genre.getName()).isEqualTo("Комедия");
                });
    }

    @Test
    void shouldReturnEmptyWhenGenreNotFound() {
        assertThat(genreStorage.findById(999L)).isEmpty();
    }

    @Test
    void shouldFindGenresByFilmId() {
        assertThat(genreStorage.findByFilmId(1L))
                .extracting("id")
                .containsExactly(4L, 6L);
    }
}