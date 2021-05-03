DROP TABLE IF EXISTS logged_out_tokens;
DROP TABLE IF EXISTS users_authorities;
DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS files;
DROP TABLE IF EXISTS users;

CREATE TABLE authorities
(
    authority VARCHAR(255) NOT NULL PRIMARY KEY
);

CREATE TABLE users
(
    username VARCHAR(255) NOT NULL PRIMARY KEY,
    enabled  BIT          NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE users_authorities
(
    username  VARCHAR(255) NOT NULL,
    authority VARCHAR(255) NOT NULL,
    PRIMARY KEY (authority, username),
    CONSTRAINT fk_authority FOREIGN KEY (authority) REFERENCES authorities (authority),
    CONSTRAINT fk_username_authority FOREIGN KEY (username) REFERENCES users (username)
);

create table files
(
    id        BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    filename  VARCHAR(255) NOT NULL,
    data      LONGBLOB     NOT NULL,
    file_type VARCHAR(255) NOT NULL,
    size      BIGINT       NOT NULL,
    username  varchar(255) NOT NULL,
    CONSTRAINT fk_username_file FOREIGN KEY (username) REFERENCES users (username)
);

create table logged_out_tokens
(
    token      VARCHAR(750) NOT NULL PRIMARY KEY,
    store_till TIMESTAMP DEFAULT NOW()
);