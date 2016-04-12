DROP FUNCTION IF EXISTS DAYSOVERDUE;
DROP PROCEDURE IF EXISTS LOANBOOK;
DROP PROCEDURE IF EXISTS RENEWBOOK;
DROP PROCEDURE IF EXISTS RENEWBOOKS;
DROP PROCEDURE IF EXISTS PAYMENT;
DROP PROCEDURE IF EXISTS RETURNBOOK;
DROP PROCEDURE IF EXISTS TOPREADERS;
DROP PROCEDURE IF EXISTS PATRONPAL;

DELIMITER //

/* Gets the days by which a a given book is overdue */
CREATE FUNCTION DAYSOVERDUE(isbn INT) RETURNS INT NOT DETERMINISTIC
BEGIN
		DECLARE due DATE;
		DECLARE diff INT DEFAULT 0;

		-- get date due
		SELECT due_date INTO due FROM book_loan WHERE book=isbn;        

		-- get difference
        SET diff = DATEDIFF(CURDATE(), due);

		IF diff < 0
			THEN SET diff = 0;
		END IF;

        RETURN diff;
END //

/* Loans the given book to the given patron */
CREATE PROCEDURE LOANBOOK(IN isbn INT, IN patron_id INT)
BEGIN
	INSERT INTO book_loan VALUES (null, patron_id, isbn, ADDDATE(CURDATE(), INTERVAL 14 DAY), 0);
END //

/* Renews the given book */
CREATE PROCEDURE RENEWBOOK(IN isbn INT)
BEGIN
	DECLARE loan INT;
	DECLARE patron INT DEFAULT 0;
	DECLARE days INT DEFAULT 0;
	DECLARE error_message VARCHAR(50) DEFAULT CONCAT("ISBN ", isbn, " is not on loan");

	-- check if book is on loan
	SELECT loan_id INTO loan FROM book_loan WHERE book=isbn AND returned <> 1;
	-- if not on loan, signal exception
	IF loan IS NULL
		THEN SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = error_message;
	END IF;

	-- Get days over due
	SET days = DAYSOVERDUE(isbn);

	-- Get patron id
	SELECT patron_id INTO patron FROM book_loan WHERE returned=0 AND book=isbn;

	-- Update fee
	UPDATE patron
 		SET fees=fees+(5*days)
 		WHERE patron_id=patron;

 	-- Update due date
 	UPDATE book_loan
 		SET due_date=ADDDATE(CURDATE(), INTERVAL 14 DAY)
 		WHERE book=isbn;
END //

/* Renews all books of a given patron */
CREATE PROCEDURE RENEWBOOKS(IN patron INT)
BEGIN
	DECLARE isbn INT DEFAULT 0;
	DECLARE finished INT DEFAULT 0;

	-- declare cursor
	DECLARE books CURSOR FOR SELECT book FROM book_loan WHERE patron_id=patron AND returned<>1;

	-- declare handlers
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET finished = 1;
	DECLARE CONTINUE HANDLER FOR SQLSTATE '45000'
	BEGIN
		show errors;
	END;

	OPEN books;
	WHILE finished = 0 DO
		FETCH books INTO isbn;
		CALL RENEWBOOK(isbn);
	END WHILE;
	CLOSE books;
END //

/* Allows patron to pay their fees. Returns any change the patron paid extra */
CREATE PROCEDURE PAYMENT(INOUT payment INT, IN patron INT)
	BEGIN
	DECLARE amount_due INT DEFAULT 0;
	DECLARE diff INT DEFAULT 0;
	SELECT fees INTO amount_due FROM patron WHERE patron_id=patron;
	SET diff = amount_due - payment;

	-- return patron's change
	IF diff <= 0 THEN
		UPDATE patron
			SET fees = 0
			WHERE patron_id=patron;
		SET payment = -1*diff;
	-- update fee to remaining amount
	ELSE
		UPDATE patron
			SET fees = diff
			WHERE patron_id=patron;
		SET payment = 0;
	END IF;
END //

/* Returns the given book */
CREATE PROCEDURE RETURNBOOK(IN isbn INT)
BEGIN
	DECLARE days INT DEFAULT 0;
	DECLARE fees INT DEFAULT 0;
	DECLARE patron INT DEFAULT 0;

	-- Get patron id
	SELECT patron_id INTO patron FROM book_loan WHERE book=isbn;
	-- Get days over due
	SET days = DAYSOVERDUE(isbn);
	-- Get fees
	SELECT fees INTO fees FROM patron WHERE patron_id=patron;
	SET fees = fees + days*5;

	-- update fees
	UPDATE patron
		SET fees=fees
		WHERE patron_id=patron;
	-- update book
	UPDATE book_loan
		SET returned=1
		WHERE book=isbn;
END//

/* Gets the patrons with the most books read for each genre */
CREATE PROCEDURE TOPREADERS()
BEGIN
	SELECT top_readers.genre_id AS 'Genre ID', genre_name AS 'Genre', top_readers.patron_id AS 'Patron ID', Firstname, Lastname
	FROM top_readers
	INNER JOIN patron ON top_readers.patron_id=patron.patron_id
	INNER JOIN genre ON top_readers.genre_id=genre.genre_id
    ORDER BY top_readers.genre_id;
END //

/* Gets patron with most similar taste in books as given patron */
CREATE PROCEDURE PATRONPAL(IN patron_number INT, OUT patron_pal_id INT)
BEGIN
	-- variables
	DECLARE patron_count INT DEFAULT 0;
	DECLARE same_books, max_same_books INT DEFAULT 0;

	SELECT COUNT(patron_id) INTO patron_count
	FROM patron;

	WHILE patron_count > 0 DO
		-- get number of books shared by another patron
		SELECT COUNT(loan_id) INTO same_books
			FROM book_loan
			WHERE book IN (
					SELECT book
						FROM book_loan
						WHERE patron_id=patron_count
					)
			AND patron_id <> patron_number;
        
		IF same_books > max_same_books AND same_books IS NOT NULL THEN
			SET max_same_books = same_books;
			SET patron_pal_id = patron_count;
		END IF;

		SET patron_count = patron_count - 1;
	END WHILE;
END //
