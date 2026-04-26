INSERT INTO mpa_rating (id, name, comment) VALUES (1, 'G', 'Нет возрастных ограничений');
INSERT INTO mpa_rating (id, name, comment) VALUES (2, 'PG', 'Рекомендуется смотреть с родителями');
INSERT INTO mpa_rating (id, name, comment) VALUES (3, 'PG-13', 'До 13 не рекомендуется');
INSERT INTO mpa_rating (id, name, comment) VALUES (4, 'R', 'До 17 с родителями');
INSERT INTO mpa_rating (id, name, comment) VALUES (5, 'NC-17', '18+');


INSERT INTO genres (id, name) VALUES (1, 'Комедия');
INSERT INTO genres (id, name) VALUES (2, 'Драма');
INSERT INTO genres (id, name) VALUES (3, 'Мультфильм');
INSERT INTO genres (id, name) VALUES (4, 'Триллер');
INSERT INTO genres (id, name) VALUES (5, 'Документальный');
INSERT INTO genres (id, name) VALUES (6, 'Боевик');

INSERT INTO users (email, login, name, birthday)
VALUES ( 'test@mail.com', 'test', 'Test User', '1990-01-01');
INSERT INTO users (email, login, name, birthday)
VALUES ('test2@mail.com', 'test2', 'Test User', '1990-01-01');
INSERT INTO users (email, login, name, birthday)
VALUES ( 'test3@mail.com', 'test3', 'Test User', '1990-01-01');
INSERT INTO users (email, login, name, birthday)
VALUES ( 'test4@mail.com', 'test4', 'Test User', '1990-01-01');
INSERT INTO users (email, login, name, birthday)
VALUES ('test5@mail.com', 'test5', 'Test User', '1990-01-01');


INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('Матрица', 'Фильм про реальность и выбор', '1999-03-31', 136, 4);
INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('Шрек', 'Мультфильм про зелёного огра', '2001-04-22', 90, 1);
INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('Интерстеллар', 'Фантастическая драма о космосе и времени', '2014-11-06', 169, 3);
INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('Криминальное чтиво', 'Криминальная история с нелинейным сюжетом', '1994-10-14', 154, 4);
INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('Земля', 'Документальный фильм о природе', '2007-09-10', 90, 1);

INSERT INTO film_genre (film_id, genre_id) VALUES (1, 4);
INSERT INTO film_genre (film_id, genre_id) VALUES (1, 6);
INSERT INTO film_genre (film_id, genre_id) VALUES (2, 1);
INSERT INTO film_genre (film_id, genre_id) VALUES (2, 3);
INSERT INTO film_genre (film_id, genre_id) VALUES (3, 2);
INSERT INTO film_genre (film_id, genre_id) VALUES (4, 2);
INSERT INTO film_genre (film_id, genre_id) VALUES (4, 6);
INSERT INTO film_genre (film_id, genre_id) VALUES (5, 5);

INSERT INTO film_like (film_id, user_id) VALUES (1, 1);
INSERT INTO film_like (film_id, user_id) VALUES (1, 2);
INSERT INTO film_like (film_id, user_id) VALUES (1, 3);
INSERT INTO film_like (film_id, user_id) VALUES (2, 1);
INSERT INTO film_like (film_id, user_id) VALUES (2, 4);
INSERT INTO film_like (film_id, user_id) VALUES (3, 1);
INSERT INTO film_like (film_id, user_id) VALUES (3, 2);
INSERT INTO film_like (film_id, user_id) VALUES (4, 5);

INSERT INTO friendship (user_id, friend_id, confirmed) VALUES (1, 2, true);
INSERT INTO friendship (user_id, friend_id, confirmed) VALUES (2, 1, true);
INSERT INTO friendship (user_id, friend_id, confirmed) VALUES (1, 3, false);
INSERT INTO friendship (user_id, friend_id, confirmed) VALUES (4, 1, false);
INSERT INTO friendship (user_id, friend_id, confirmed) VALUES (2, 3, true);
INSERT INTO friendship (user_id, friend_id, confirmed) VALUES (3, 2, true);