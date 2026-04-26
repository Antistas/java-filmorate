package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    void shouldFindUserById() {
        Optional<User> userOptional = userStorage.findById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void shouldCreateUser() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("test666");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.create(user);
        assertThat(createdUser.getId()).isNotNull();
        Optional<User> fromDb = userStorage.findById(createdUser.getId());
        assertThat(fromDb).isPresent();
    }

    @Test
    void shouldUpdateUser() {
        User user = new User();
        user.setEmail("update@mail.com");
        user.setLogin("update");
        user.setName("Old Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.create(user);

        createdUser.setName("New Name");
        userStorage.update(createdUser);

        Optional<User> updatedUser = userStorage.findById(createdUser.getId());

        assertThat(updatedUser)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u.getName()).isEqualTo("New Name")
                );
    }

    @Test
    void shouldFindAllUsers() {
        List<User> users = userStorage.findAll().stream().toList();
        assertThat(users).isNotNull();
    }
}
