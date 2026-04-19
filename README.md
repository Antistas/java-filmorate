# java-filmorate
Template repository for Filmorate project.


```mermaid
erDiagram
    USER {
        BIGINT id PK
        VARCHAR email UK
        VARCHAR login
        VARCHAR name
        DATE birthday
    }

    FILM {
        BIGINT id PK
        VARCHAR name
        VARCHAR description
        DATE release_date
        INT duration
        INT mpa_id FK
    }

    GENRE {
        INT id PK
        VARCHAR name UK
    }

    MPA_RATING {
        INT id PK
        VARCHAR name UK
        VARCHAR comment
    }

    FILM_GENRE {
        BIGINT film_id PK, FK
        INT genre_id PK, FK
    }

    FILM_LIKE {
        BIGINT film_id PK, FK
        BIGINT user_id PK, FK
    }

    FRIENDSHIP {
        BIGINT user_id PK, FK
        BIGINT friend_id PK, FK
        BOOLEAN confirmed
    }

    MPA_RATING ||--o{ FILM : rates
    FILM ||--o{ FILM_GENRE : has
    GENRE ||--o{ FILM_GENRE : used_in
    USER ||--o{ FILM_LIKE : puts
    FILM ||--o{ FILM_LIKE : receives
    USER ||--o{ FRIENDSHIP : initiates
    USER ||--o{ FRIENDSHIP : receives

```

Или
https://app.quickdatabasediagrams.com/#/
![DB Schema](docs/QuickDBD-export.png)
```
User as u
------
id PK bigint
email varchar(255) UNIQUE
login varchar(255)
name varchar(255)
birthday date

Film as f
------
id PK bigint
name varchar(255)
description varchar(200)
release_date date
duration int
mpa_id int FK >- mpa.id

Genre as g
------
id PK int
name varchar(100) UNIQUE

MpaRating as mpa
------
id PK int
name varchar(10) UNIQUE
comment varchar(255)


FilmGenre as fg
------
film_id PK bigint FK >- f.id
genre_id PK int FK >- g.id

FilmLike as fl
------
film_id PK bigint FK >- f.id
user_id PK bigint FK >- u.id

Friendship as fr
------
user_id PK bigint FK >- u.id
friend_id PK bigint FK >- u.id
confirmed bool
```


