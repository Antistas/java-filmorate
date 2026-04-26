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
        assertThat(fromDb)
                .isPresent()
                .hasValueSatisfying(savedUser -> {
                    assertThat(savedUser.getEmail()).isEqualTo("test@mail.ru");
                    assertThat(savedUser.getLogin()).isEqualTo("test666");
                    assertThat(savedUser.getName()).isEqualTo("Test User");
                    assertThat(savedUser.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
                });
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
    void shouldUpdateAllUserFields() {
        User user = new User();
        user.setEmail("before@mail.com");
        user.setLogin("before");
        user.setName("Before Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser = userStorage.create(user);

        createdUser.setEmail("after@mail.com");
        createdUser.setLogin("after");
        createdUser.setName("After Name");
        createdUser.setBirthday(LocalDate.of(1995, 5, 5));
        userStorage.update(createdUser);
        Optional<User> updatedUser = userStorage.findById(createdUser.getId());
        assertThat(updatedUser)
                .isPresent()
                .hasValueSatisfying(savedUser -> {
                    assertThat(savedUser.getEmail()).isEqualTo("after@mail.com");
                    assertThat(savedUser.getLogin()).isEqualTo("after");
                    assertThat(savedUser.getName()).isEqualTo("After Name");
                    assertThat(savedUser.getBirthday()).isEqualTo(LocalDate.of(1995, 5, 5));
                });
    }

    @Test
    void shouldFindAllUsers() {
        List<User> users = userStorage.findAll().stream().toList();
        assertThat(users).isNotNull();
    }

    @Test
    void shouldReturnEmptyOptionalWhenUserNotFound() {
        Optional<User> userOptional = userStorage.findById(999999L);
        assertThat(userOptional).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenEmailBelongsToAnotherUser() {
        User firstUser = new User();
        firstUser.setEmail("first-duplicate@mail.com");
        firstUser.setLogin("firstduplicate");
        firstUser.setName("First Duplicate");
        firstUser.setBirthday(LocalDate.of(1990, 1, 1));
        User createdFirstUser = userStorage.create(firstUser);

        User secondUser = new User();
        secondUser.setEmail("second-duplicate@mail.com");
        secondUser.setLogin("secondduplicate");
        secondUser.setName("Second Duplicate");
        secondUser.setBirthday(LocalDate.of(1991, 2, 2));
        User createdSecondUser = userStorage.create(secondUser);

        boolean emailDuplicated = userStorage.checkEmailDublication(
                createdSecondUser.getId(),
                createdFirstUser.getEmail()
        );

        assertThat(emailDuplicated).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEmailBelongsToSameUser() {
        User user = new User();
        user.setEmail("same-user@mail.com");
        user.setLogin("sameuser");
        user.setName("Same User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser = userStorage.create(user);

        boolean emailDuplicated = userStorage.checkEmailDublication(
                createdUser.getId(),
                createdUser.getEmail()
        );

        assertThat(emailDuplicated).isFalse();
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        User user = new User();
        user.setEmail("existing-user@mail.com");
        user.setLogin("existinguser");
        user.setName("Existing User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser = userStorage.create(user);

        boolean emailDuplicated = userStorage.checkEmailDublication(
                createdUser.getId(),
                "free-email@mail.com"
        );

        assertThat(emailDuplicated).isFalse();
    }

}
