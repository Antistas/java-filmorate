package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Instant;

/**
 * Film.
 */

@Data
public class Film {
    Long id;
    String name;
    String description;
    Instant releaseDate;
    int duration;

}
