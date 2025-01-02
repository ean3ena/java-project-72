DROP TABLE IF EXISTS urls;

CREATE TABLE urls (
    id BIGSERIAL PRIMARY KEY,
    name varchar(255) NOT NULL UNIQUE,
    created_at timestamp NOT NULL
);