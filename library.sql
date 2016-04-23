DROP TABLE IF EXISTS book_authors;
DROP TABLE IF EXISTS author;
DROP TABLE IF EXISTS book_loan;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS top_readers;
DROP TABLE IF EXISTS genre;
DROP TABLE IF EXISTS patron;
DROP TABLE IF EXISTS users;

CREATE TABLE `author` (
  `author_id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `firstname` VARCHAR(255) NOT NULL,
  `lastname` VARCHAR(255) NOT NULL
);

CREATE TABLE `genre` (
  `genre_id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `genre_name` VARCHAR(255) NOT NULL
);

CREATE TABLE `book` (
  `isbn` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `book_title` VARCHAR(255) NOT NULL,
  `publication_date` DATE,
  `genre` INTEGER NOT NULL
);

CREATE INDEX `idx_book__genre` ON `book` (`genre`);

ALTER TABLE `book` ADD CONSTRAINT `fk_book__genre` FOREIGN KEY (`genre`) REFERENCES `genre` (`genre_id`);

CREATE TABLE `book_authors` (
  `author` INTEGER NOT NULL,
  `book` INTEGER NOT NULL,
  PRIMARY KEY (`author`, `book`)
);

CREATE INDEX `idx_book_authors__book` ON `book_authors` (`book`);

ALTER TABLE `book_authors` ADD CONSTRAINT `fk_book_authors__author` FOREIGN KEY (`author`) REFERENCES `author` (`author_id`);
ALTER TABLE `book_authors` ADD CONSTRAINT `fk_book_authors__book` FOREIGN KEY (`book`) REFERENCES `book` (`isbn`);

CREATE TABLE `patron` (
  `patron_id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `firstname` VARCHAR(255) NOT NULL,
  `lastname` VARCHAR(255) NOT NULL,
  `fees` INTEGER NOT NULL,
  `email` VARCHAR(255) NOT NULL
);

CREATE TABLE `book_loan` (
  `loan_id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `patron_id` INTEGER NOT NULL,
  `book` INTEGER NOT NULL,
  `due_date` DATE NOT NULL,
  `returned` BOOLEAN NOT NULL
);

CREATE INDEX `idx_book_loan__book` ON `book_loan` (`book`);
CREATE INDEX `idx_book_loan__patron_id` ON `book_loan` (`patron_id`);

ALTER TABLE `book_loan` ADD CONSTRAINT `fk_book_loan__book` FOREIGN KEY (`book`) REFERENCES `book` (`isbn`);
ALTER TABLE `book_loan` ADD CONSTRAINT `fk_book_loan__patron_id` FOREIGN KEY (`patron_id`) REFERENCES `patron` (`patron_id`);

CREATE TABLE `users` (
  `userid` VARCHAR(50) PRIMARY KEY,
  `salt` VARCHAR(30) NOT NULL,
  `hash` VARBINARY(256) NOT NULL
);

CREATE TABLE `top_readers` (
  `genre_id` INT UNIQUE,
  `patron_id` INT
);

ALTER TABLE `top_readers` ADD CONSTRAINT FOREIGN KEY (`genre_id`) REFERENCES `genre` (`genre_id`);
ALTER TABLE `top_readers` ADD CONSTRAINT FOREIGN KEY (`patron_id`) REFERENCES `patron` (`patron_id`);

COMMIT;
