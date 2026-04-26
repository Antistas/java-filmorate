package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRatingRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("mpaDbStorage")
@RequiredArgsConstructor
public class MpaRatingDbStorage implements MpaRatingStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaRatingRowMapper mpaRatingRowMapper;

    @Override
    public Collection<MpaRating> findAll() {
        return jdbcTemplate.query("SELECT * FROM mpa_rating", mpaRatingRowMapper);
    }

    @Override
    public Optional<MpaRating> findById(Long id) {
        List<MpaRating> mpa = jdbcTemplate.query("SELECT * FROM mpa_rating WHERE id = ?", mpaRatingRowMapper, id);
        return mpa.stream().findFirst();
    }
}
