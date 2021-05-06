--liquibase formatted sql
--changeset user:0.2-1
INSERT INTO authorities (authority)
VALUES ('files');

--changeset user:0.2-2
INSERT INTO users (username, password, enabled)
VALUES ('john', '$2a$10$II.53w5gMxP0XtI230a6xui2MEiP/L1jpslsAjS69trpk3vmjUFWi', 1),
       ('ivan', '$2a$10$hwrGt/oUbYXcHtwOE1YI2ufwvXbqf/3zo5PqjIye5hnL/cG3ohPme', 1);

--changeset user:0.2-3
INSERT INTO users_authorities (username, authority)
VALUES ('john', 'files'),
       ('ivan', 'files');