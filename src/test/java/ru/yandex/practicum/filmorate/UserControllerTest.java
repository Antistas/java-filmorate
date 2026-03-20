package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    // пользователь добавляется с пустым именем
    @Test
    void shouldSetLoginAsNameWhenNameIsBlank() {
        UserController controller = new UserController();

        User user = new User();
        user.setEmail("rustam@mail.com");
        user.setLogin("rustam");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = controller.postUser(user);

        assertNotNull(createdUser.getId());
        assertEquals("rustam", createdUser.getName());
    }

    // исключение при добавлении пользователя с таким же емейлом
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExistsOnCreate() {
        UserController controller = new UserController();

        User firstUser = new User();
        firstUser.setEmail("rustam@mail.com");
        firstUser.setLogin("rustam");
        firstUser.setName("Rustam");
        firstUser.setBirthday(LocalDate.of(1990, 1, 1));
        controller.postUser(firstUser);

        User secondUser = new User();
        secondUser.setEmail("rustam@mail.com");
        secondUser.setLogin("rustam2");
        secondUser.setName("Rustam 2");
        secondUser.setBirthday(LocalDate.of(1992, 2, 2));

        assertThrows(ValidationException.class, () -> controller.postUser(secondUser));
    }

    // обновляем несуществующего пользователя
    @Test
    void shouldThrowExceptionWhenUpdatingUnknownUser() {
        UserController controller = new UserController();

        User user = new User();
        user.setId(666L);
        user.setEmail("rustam@mail.com");
        user.setLogin("rustam");
        user.setName("Rustam");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(NotFoundException.class, () -> controller.updateUser(user));
    }

    // Исключение когда емейл пустой
    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        UserController controller = new UserController();

        User user = new User();
        user.setEmail("");
        user.setLogin("rustam");
        user.setName("Rustam");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.postUser(user));
        assertTrue(exception.getMessage().contains("Email") || exception.getMessage().contains("email"));
    }

    // логин содержит пробелы
    @Test
    void shouldThrowExceptionWhenLoginContainsSpaces() {
        UserController controller = new UserController();

        User user = new User();
        user.setEmail("rustam@mail.com");
        user.setLogin("ru stam");
        user.setName("Rustam");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.postUser(user));
        assertTrue(exception.getMessage().contains("Login"));
    }

    // Дата рождения в будущем
    @Test
    void shouldThrowExceptionWhenBirthdayIsInFuture() {
        UserController controller = new UserController();

        User user = new User();
        user.setEmail("rustam@mail.com");
        user.setLogin("rustam");
        user.setName("Rustam");
        user.setBirthday(LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.postUser(user));
        assertTrue(exception.getMessage().contains("будущем"));
    }

    // второй пользователь вписывает существующий емейл
    @Test
    void shouldThrowExceptionWhenUpdatingUserWithDuplicateEmail() {
        UserController controller = new UserController();

        User firstUser = new User();
        firstUser.setEmail("first@mail.com");
        firstUser.setLogin("first");
        firstUser.setName("First");
        firstUser.setBirthday(LocalDate.of(1990, 1, 1));
        User createdFirstUser = controller.postUser(firstUser);

        User secondUser = new User();
        secondUser.setEmail("second@mail.com");
        secondUser.setLogin("second");
        secondUser.setName("Second");
        secondUser.setBirthday(LocalDate.of(1992, 2, 2));
        User createdSecondUser = controller.postUser(secondUser);

        createdSecondUser.setEmail(createdFirstUser.getEmail());

        assertThrows(ValidationException.class, () -> controller.updateUser(createdSecondUser));
    }

}




