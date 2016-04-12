DROP TABLE IF EXISTS book_authors;
DROP TABLE IF EXISTS author;
DROP TABLE IF EXISTS book_loan;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS genre;
DROP TABLE IF EXISTS patron;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS top_readers;

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

INSERT INTO genre (genre_name) VALUES ('horror');
INSERT INTO genre (genre_name) VALUES ('scifi');
INSERT INTO genre (genre_name) VALUES ('fantasy');
INSERT INTO genre (genre_name) VALUES ('biography');
INSERT INTO genre (genre_name) VALUES ('childrens');

INSERT INTO author (firstname, lastname) VALUES ('George','Martin');
INSERT INTO author (firstname, lastname) VALUES ('JK','Rowling');
INSERT INTO author (firstname, lastname) VALUES ('Doc','Seuss');
INSERT INTO author (firstname, lastname) VALUES ('Isaac','Asimov');

INSERT INTO book VALUES (12345,'Harry Potter and some rock','2008-02-01',3);
INSERT INTO book VALUES (12346,'Harry Potter and some cup','2005-03-12',3);
INSERT INTO book VALUES (12347,'Harry Potter and some fugitive','2009-06-16',3);
INSERT INTO book VALUES (12348,'Harry Potter and Jon Snows ultimate crossover','2012-02-14',1);
INSERT INTO book VALUES (12349,'Game of Chairs','2012-02-14',3);
INSERT INTO book VALUES (12350,'Silly Nonsense','2012-02-28',5);
INSERT INTO book VALUES (12351,'My Robot','2014-09-24',2);

INSERT INTO book_authors VALUES (2,12345);
INSERT INTO book_authors VALUES (2,12346);
INSERT INTO book_authors VALUES (2,12347);
INSERT INTO book_authors VALUES (2,12348);
INSERT INTO book_authors VALUES (1,12348);
INSERT INTO book_authors VALUES (1,12349);
INSERT INTO book_authors VALUES (3,12350);
INSERT INTO book_authors VALUES (4,12351);

INSERT INTO patron (firstname, lastname,fees,email) VALUES ('Joe','Sho',0,'joes@gmail.com');
INSERT INTO patron (firstname, lastname,fees,email) VALUES ('Edna','Carmichael',10,'ednak@gmail.com');
INSERT INTO patron (firstname, lastname,fees,email) VALUES ('Timmytim','Tim',0,'timtimtim@gmail.com');

INSERT INTO book_loan (patron_id,book,due_date,returned) VALUES (1,12345,'2013-02-14',true);
INSERT INTO book_loan (patron_id,book,due_date,returned) VALUES (1,12346,'2013-03-14',true);
INSERT INTO book_loan (patron_id,book,due_date,returned) VALUES (2,12348,'2015-09-18',false);
INSERT INTO book_loan (patron_id,book,due_date,returned) VALUES (3,12350,'2015-04-11',false);

INSERT INTO top_readers VALUES (1,2);
INSERT INTO top_readers VALUES (3,1);
INSERT INTO top_readers VALUES (5,3);

COMMIT;
