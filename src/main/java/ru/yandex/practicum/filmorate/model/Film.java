package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    Long id;

    private Set<Long> likes = new HashSet<>();

    @NotBlank(message = "Название фильма не может быть пустым")
    String name;

    @Size(max = 200, message = "Длина описания не может превышать 200 символов")
    String description;

    @NotNull
    LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    @NotNull
    Integer duration;
}
