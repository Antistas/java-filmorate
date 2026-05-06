package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.storage.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRatingRowMapper;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaRatingDbStorage.class, MpaRatingRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaRatingDbStorageTest {

    private final MpaRatingDbStorage mpaRatingStorage;

    @Test
    void shouldFindAllMpaRatings() {
        assertThat(mpaRatingStorage.findAll()).hasSize(5);
    }

    @Test
    void shouldFindMpaRatingById() {
        assertThat(mpaRatingStorage.findById(1L))
                .isPresent()
                .hasValueSatisfying(mpa -> {
                    assertThat(mpa.getId()).isEqualTo(1L);
                    assertThat(mpa.getName()).isEqualTo("G");
                });
    }

    @Test
    void shouldReturnEmptyWhenMpaRatingNotFound() {
        assertThat(mpaRatingStorage.findById(999L)).isEmpty();
    }
}