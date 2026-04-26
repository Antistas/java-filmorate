package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(LikeDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class LikeDbStorageTest {

    private final LikeDbStorage likeStorage;

    @Test
    void shouldAddLike() {
        likeStorage.addLike(5L, 5L);
        assertThat(likeStorage.isLiked(5L, 5L)).isTrue();
    }

    @Test
    void shouldRemoveLike() {
        likeStorage.addLike(5L, 5L);
        assertThat(likeStorage.isLiked(5L, 5L)).isTrue();
        likeStorage.removeLike(5L, 5L);
        assertThat(likeStorage.isLiked(5L, 5L)).isFalse();
    }

    @Test
    void shouldReturnFalseWhenLikeDoesNotExist() {
        assertThat(likeStorage.isLiked(5L, 999L)).isFalse();
    }

}
