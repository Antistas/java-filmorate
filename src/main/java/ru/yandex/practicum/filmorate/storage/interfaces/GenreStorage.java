package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {
    Genre create(Genre genre);

    Genre update(Genre genre);

    Collection<Genre> findAll();

    Optional<Genre> findById(Long id);

    Collection<Genre> findByFilmId(Long id);
}
