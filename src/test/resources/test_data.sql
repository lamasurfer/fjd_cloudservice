INSERT INTO authorities (authority)
VALUES ('files');

INSERT INTO users (username, password, enabled)
VALUES ('user', '$2a$10$JodSAtU9oFf/2unv6w0PeeC.YqvutL1IQ09mT9CF3C0Zu2vAR.DKi', 1);

INSERT INTO users_authorities (username, authority)
VALUES ('user', 'files');
