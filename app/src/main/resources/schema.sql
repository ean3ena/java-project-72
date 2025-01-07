DROP TABLE IF EXISTS urls;
DROP TABLE IF EXISTS url_checks;

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
    url_id BIGSERIAL NOT NULL,
    created_at timestamp NOT NULL
);