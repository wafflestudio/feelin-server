CREATE DATABASE feelin;
CREATE USER 'feelin-test'@'localhost' IDENTIFIED BY 'feelin';
GRANT ALL PRIVILEGES ON feelin.* TO 'feelin-test'@'localhost';
