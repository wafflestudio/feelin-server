CREATE DATABASE feelinDB;
CREATE USER 'feelin'@'localhost' IDENTIFIED BY 'feelin';
GRANT ALL PRIVILEGES ON feelinDB.* TO 'feelin'@'localhost';
