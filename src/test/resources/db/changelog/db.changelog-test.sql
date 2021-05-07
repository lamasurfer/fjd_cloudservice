--liquibase formatted sql
--changeset user:test-1
INSERT INTO authorities (authority)
VALUES ('files');

--changeset user:test-2
INSERT INTO users (username, password, enabled)
VALUES ('john', '$2a$10$II.53w5gMxP0XtI230a6xui2MEiP/L1jpslsAjS69trpk3vmjUFWi', 1),
       ('user', '$2a$10$8Ys2ss9XM2hYJOwEVCYf.OdBzlvMQiY1XunvC0fs.MIelUkIEJ.hi', 1),
       ('user1', '$2a$10$b3jerUA1bXSaM6GyTR8b5OlHGkvTUjp7QOCZsaQakJq6gWp/SOyCa', 1),
       ('user2', '$2a$10$SpoOX7hDw139xJj99SCb7O02dLj8fR4yS250UGGKtCYhTL0F6iWIO', 1);

--changeset user:test-3
INSERT INTO users_authorities (username, authority)
VALUES ('john', 'files'),
       ('user', 'files'),
       ('user1', 'files'),
       ('user2', 'files');