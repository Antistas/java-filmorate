package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MpaRating {
    private Long id;

    @NotBlank(message = "Название рейтинга не может быть пустым")
    private String name;

    private String comment;
}
