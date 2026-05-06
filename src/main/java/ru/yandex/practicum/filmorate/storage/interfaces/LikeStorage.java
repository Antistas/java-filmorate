package ru.yandex.practicum.filmorate.storage.interfaces;

public interface LikeStorage {

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    boolean isLiked(Long filmId, Long userId);
}
