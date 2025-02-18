CREATE TABLE IF NOT EXISTS media_files
(
    absolute_path VARCHAR PRIMARY KEY NOT NULL UNIQUE,
    shoot_at      TIMESTAMP           NULL,
    origin        VARCHAR             NOT NULL,
    checksum      VARCHAR             NULL
);
