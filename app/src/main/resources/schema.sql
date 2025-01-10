DROP TABLE IF EXISTS url_checks;
DROP TABLE IF EXISTS urls;

CREATE TABLE urls (
    id BIGSERIAL PRIMARY KEY,
    name varchar(255) NOT NULL UNIQUE,
    created_at timestamp NOT NULL
);

CREATE TABLE url_checks (
    id BIGSERIAL PRIMARY KEY,
    status_code int NOT NULL,
    title varchar(255),
    h1 varchar(255),
    description text,
    url_id BIGSERIAL REFERENCES urls(id) NOT NULL,
    created_at timestamp NOT NULL
);