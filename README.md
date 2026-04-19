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
