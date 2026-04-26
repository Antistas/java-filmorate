package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    public GenreService(@Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Collection<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre create(Genre genre) {
        return genreStorage.create(genre);
    }

    public Genre update(Genre genre) {
        if (genre.getId() == null) {
            log.warn("Передан пустой id = {}", genre.getId());
            throw new ValidationException("Id должен быть указан");
        }

        getGenreOrThrow(genre.getId());
        return genreStorage.update(genre);
    }

    public Genre findById(Long id) {
        return getGenreOrThrow(id);
    }

    private Genre getGenreOrThrow(Long genreId) {
        Optional<Genre> genre = genreStorage.findById(genreId);
        if (genre.isEmpty()) {
            throw new NotFoundException("Жанр c id = " + genreId + " не найден");
        }
        return genre.get();
    }

    public Collection<Genre> getGenresByFilmId(Long filmId) {
        return genreStorage.findByFilmId(filmId);
    }
}
