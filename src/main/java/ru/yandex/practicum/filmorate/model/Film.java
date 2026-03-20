package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Film.
 */

@Data
public class Film {
    Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    String name;

    @Size(max = 200, message = "Длина описания не может превышать 200 символов")
    String description;
    LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    Integer duration;
}
