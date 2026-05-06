package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class User {
    Long id;

    private Map<Long, FriendshipStatus> friends = new HashMap<>();

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email некорректен")
    String email;

    @NotBlank(message = "Login не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Login не должен содержать пробелы")
    String login;

    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    @NotNull
    LocalDate birthday;
}
