CREATE TABLE files
(
    filename      VARCHAR(255) NOT NULL,
    date          TIMESTAMP(6) NOT NULL,
    file_content  BYTEA        NOT NULL,
    size          BIGINT       NOT NULL,
    user_username VARCHAR(255),
    PRIMARY KEY (filename)
);
