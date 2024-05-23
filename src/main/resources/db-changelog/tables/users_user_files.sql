CREATE TABLE users_user_files
(
    user_username       VARCHAR(255) NOT NULL,
    user_files_filename VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_username, user_files_filename),
    FOREIGN KEY (user_username) REFERENCES users(username),
    FOREIGN KEY (user_files_filename) REFERENCES files(filename)
);
