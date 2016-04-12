DROP TRIGGER IF EXISTS after_book_loan_insert;

DELIMITER //

CREATE TRIGGER after_book_loan_insert AFTER INSERT ON book_loan FOR EACH ROW
BEGIN
	DECLARE genre_number, new_top_reader, current_top_reader INT;

	-- get genre of last book inserted
	SELECT genre INTO genre_number
		FROM book_loan
        	INNER JOIN book ON isbn=book
        	ORDER BY loan_id
        	DESC LIMIT 1;

	-- Get new top patron for genre
	SELECT patron_id INTO new_top_reader
		FROM book
		INNER JOIN book_loan ON book=isbn
		WHERE genre=genre_number
		GROUP BY genre, patron_id
		ORDER BY genre, count(patron_id)
		DESC LIMIT 1;
        
	-- Get current top patron for genre
	SELECT patron_id INTO current_top_reader
		FROM top_readers
        	WHERE genre_id=genre_number;
	
	-- if the top patron has changed
	IF current_top_reader <> new_top_reader THEN
		UPDATE top_readers SET patron_id=new_top_reader WHERE genre_id=genre_number;
	END IF;
	IF current_top_reader IS NULL THEN
		INSERT INTO top_readers VALUES (genre_number, new_top_reader);
	END IF;
END //
