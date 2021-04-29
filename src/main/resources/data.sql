INSERT INTO authorities (authority)
VALUES ('files');

INSERT INTO users (username, password, enabled)
VALUES ('john', '$2a$10$II.53w5gMxP0XtI230a6xui2MEiP/L1jpslsAjS69trpk3vmjUFWi', 1),
       ('ivan', '$2a$10$hwrGt/oUbYXcHtwOE1YI2ufwvXbqf/3zo5PqjIye5hnL/cG3ohPme', 1);

INSERT INTO users_authorities (username, authority)
VALUES ('john', 'files'),
       ('ivan', 'files');