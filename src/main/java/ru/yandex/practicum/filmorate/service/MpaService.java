package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaRatingStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class MpaService {

    private final MpaRatingStorage mpaStorage;

    public MpaService(@Qualifier("mpaDbStorage") MpaRatingStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Collection<MpaRating> findAll() {
        return mpaStorage.findAll();
    }

    public MpaRating findById(Long id) {
        return getMpaOrThrow(id);
    }

    private MpaRating getMpaOrThrow(Long mpaRatingId) {
        Optional<MpaRating> mpa = mpaStorage.findById(mpaRatingId);
        if (mpa.isEmpty()) {
            throw new NotFoundException("Рейтинг c id = " + mpaRatingId + " не найден");
        }
        return mpa.get();
    }
}
