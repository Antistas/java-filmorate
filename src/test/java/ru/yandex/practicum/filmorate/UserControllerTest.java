package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();

        UserService userService = new UserService(userStorage);
        FilmService filmService = new FilmService(filmStorage, userService);

        userController = new UserController(userService);
    }

    // пользователь добавляется с пустым именем
    @Test
    void shouldSetLoginAsNameWhenNameIsBlank() {
        User user = new User();
        user.setEmail("rustam@mail.com");
        user.setLogin("rustam");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userController.postUser(user);

        assertNotNull(createdUser.getId());
        assertEquals("rustam", createdUser.getName());
    }

    // исключение при добавлении пользователя с таким же емейлом
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExistsOnCreate() {

        User firstUser = new User();
        firstUser.setEmail("rustam@mail.com");
        firstUser.setLogin("rustam");
        firstUser.setName("Rustam");
        firstUser.setBirthday(LocalDate.of(1990, 1, 1));
        userController.postUser(firstUser);

        User secondUser = new User();
        secondUser.setEmail("rustam@mail.com");
        secondUser.setLogin("rustam2");
        secondUser.setName("Rustam 2");
        secondUser.setBirthday(LocalDate.of(1992, 2, 2));

        assertThrows(ValidationException.class, () -> userController.postUser(secondUser));
    }

    // обновляем несуществующего пользователя
    @Test
    void shouldThrowExceptionWhenUpdatingUnknownUser() {
        User user = new User();
        user.setId(666L);
        user.setEmail("rustam@mail.com");
        user.setLogin("rustam");
        user.setName("Rustam");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(NotFoundException.class, () -> userController.updateUser(user));
    }

    // второй пользователь вписывает существующий емейл
    @Test
    void shouldThrowExceptionWhenUpdatingUserWithDuplicateEmail() {
        User firstUser = new User();
        firstUser.setEmail("first@mail.com");
        firstUser.setLogin("first");
        firstUser.setName("First");
        firstUser.setBirthday(LocalDate.of(1990, 1, 1));
        User createdFirstUser = userController.postUser(firstUser);

        User secondUser = new User();
        secondUser.setEmail("second@mail.com");
        secondUser.setLogin("second");
        secondUser.setName("Second");
        secondUser.setBirthday(LocalDate.of(1992, 2, 2));
        User createdSecondUser = userController.postUser(secondUser);

        createdSecondUser.setEmail(createdFirstUser.getEmail());

        assertThrows(ValidationException.class, () -> userController.updateUser(createdSecondUser));
    }

    @Test
    void shouldAddFriend() {
        User firstUser = new User();
        firstUser.setEmail("first@mail.com");
        firstUser.setLogin("first");
        firstUser.setName("First");
        firstUser.setBirthday(LocalDate.of(1990, 1, 1));
        User createdFirstUser = userController.postUser(firstUser);

        User secondUser = new User();
        secondUser.setEmail("second@mail.com");
        secondUser.setLogin("second");
        secondUser.setName("Second");
        secondUser.setBirthday(LocalDate.of(1992, 2, 2));
        User createdSecondUser = userController.postUser(secondUser);

        userController.addFriend(createdFirstUser.getId(), createdSecondUser.getId());
        assertEquals(1, createdFirstUser.getFriends().size());
        assertEquals(1, createdSecondUser.getFriends().size());
        assertTrue(createdSecondUser.getFriends().contains(createdFirstUser.getId()));
        assertTrue(createdFirstUser.getFriends().contains(createdSecondUser.getId()));
    }

    @Test
    void shouldRemoveFriend() {
        User firstUser = new User();
        firstUser.setEmail("first@mail.com");
        firstUser.setLogin("first");
        firstUser.setName("First");
        firstUser.setBirthday(LocalDate.of(1990, 1, 1));
        User createdFirstUser = userController.postUser(firstUser);

        User secondUser = new User();
        secondUser.setEmail("second@mail.com");
        secondUser.setLogin("second");
        secondUser.setName("Second");
        secondUser.setBirthday(LocalDate.of(1992, 2, 2));
        User createdSecondUser = userController.postUser(secondUser);
        userController.addFriend(createdFirstUser.getId(), createdSecondUser.getId());
        userController.removeFriend(createdFirstUser.getId(), createdSecondUser.getId());


        assertEquals(0, createdFirstUser.getFriends().size());
        assertEquals(0, createdSecondUser.getFriends().size());
        assertFalse(createdSecondUser.getFriends().contains(createdFirstUser.getId()));
        assertFalse(createdFirstUser.getFriends().contains(createdSecondUser.getId()));
    }

    @Test
    void shouldReturnListOfCommonFriends() {
            User firstUser = new User();
            firstUser.setEmail("first@mail.com");
            firstUser.setLogin("first");
            firstUser.setName("First");
            firstUser.setBirthday(LocalDate.of(1990, 1, 1));
            User createdFirstUser = userController.postUser(firstUser);

            User secondUser = new User();
            secondUser.setEmail("second@mail.com");
            secondUser.setLogin("second");
            secondUser.setName("Second");
            secondUser.setBirthday(LocalDate.of(1992, 2, 2));
            User createdSecondUser = userController.postUser(secondUser);

            User thirdUser = new User();
            thirdUser.setEmail("third@mail.com");
            thirdUser.setLogin("third");
            thirdUser.setName("third");
            thirdUser.setBirthday(LocalDate.of(1992, 2, 2));
            User createdThirdUser = userController.postUser(thirdUser);

            userController.addFriend(createdFirstUser.getId(), createdSecondUser.getId());
            userController.addFriend(createdThirdUser.getId(), createdSecondUser.getId());

            assertEquals(List.of(createdSecondUser),
                    userController.getCommonFriends(createdFirstUser.getId(), createdThirdUser.getId()));
    }

    @Test
    void shouldReturnEmptyListOfCommonFriends() {
        User firstUser = new User();
        firstUser.setEmail("first@mail.com");
        firstUser.setLogin("first");
        firstUser.setName("First");
        firstUser.setBirthday(LocalDate.of(1990, 1, 1));
        User createdFirstUser = userController.postUser(firstUser);

        User secondUser = new User();
        secondUser.setEmail("second@mail.com");
        secondUser.setLogin("second");
        secondUser.setName("Second");
        secondUser.setBirthday(LocalDate.of(1992, 2, 2));
        User createdSecondUser = userController.postUser(secondUser);

        User thirdUser = new User();
        thirdUser.setEmail("third@mail.com");
        thirdUser.setLogin("third");
        thirdUser.setName("third");
        thirdUser.setBirthday(LocalDate.of(1992, 2, 2));
        User createdThirdUser = userController.postUser(thirdUser);

        User fourthUser = new User();
        fourthUser.setEmail("fourth@mail.com");
        fourthUser.setLogin("fourth");
        fourthUser.setName("fourth");
        fourthUser.setBirthday(LocalDate.of(1992, 2, 2));
        User createdFourthUser = userController.postUser(fourthUser);

        userController.addFriend(createdFirstUser.getId(), createdSecondUser.getId());
        userController.addFriend(createdThirdUser.getId(), createdFourthUser.getId());

        assertEquals(List.of(),userController.getCommonFriends(createdFirstUser.getId(), createdThirdUser.getId()));
    }

}




