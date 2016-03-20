package library;

import java.sql.*;
import java.time.LocalDate;

import application.Library;
import business.Book;
import business.Patron;

public class LibraryDatabase extends Library {

	/**
	 * Creates a book object from the given ISBN. If the ISBN is not in the
	 * database, null will be returned
	 * 
	 * @param isbn
	 *            The ISBN of the book
	 * @return The book object. Null if the ISBN does not exist
	 */
	public ResultSet getBook(int isbn) {
		PreparedStatement stmt = db.prepareStatement(
				"SELECT isbn AS 'ISBN', book_title AS 'Title', publication_date AS 'Publication Date', genre_name AS 'Genre' "
				+ "FROM book INNER JOIN genre ON genre = genre_id "
				+ "WHERE isbn=?;");
		try {
			stmt.setInt(1, isbn);
		} catch (SQLException e) {
		}

		return db.executeStatement(stmt);
	}

	/**
	 * Prints title, ISBN, author(s) first and last names, genre and publication
	 * date of all books in the database
	 * 
	 * @throws SQLException
	 *             if any {@code SQLException} occurs while executing statements
	 *             or reading results
	 */
	public ResultSet bookReport() {

		PreparedStatement stmt = db.prepareStatement(
				"SELECT isbn AS 'ISBN', book_title AS 'Title', publication_date AS 'Publication Date', genre_name AS 'Genre', firstname AS 'Firstname', lastname AS 'Lastname' "
				+ "FROM book "
				+ "INNER JOIN genre ON book.genre = genre.genre_id "
				+ "INNER JOIN book_authors ON isbn = book "
				+ "INNER JOIN author ON author = author_id "
				+ "ORDER BY isbn;");

		return db.executeStatement(stmt);
	}

	/**
	 * Adds a Patron to the database. If the patron already exists, nothing will
	 * happen
	 * 
	 * @param firstname
	 *            The first name of the patron
	 * @param lastname
	 *            The last name of the patron
	 * @param email
	 *            The email of the patron. Not required
	 * @return True if the patron was added, false otherwise.
	 */
	public boolean newPatron(String firstname, String lastname, String email) {
		PreparedStatement stmt;
		ResultSet rs = null;
		int patronId = -1;

		// Get next patron id
		try {
			rs = db.executeStatement("SELECT COUNT(patron_id) FROM patron;");
			rs.next();
			patronId = rs.getInt(1) + 1;
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("An error occured while getting Patron ID");
		}

		stmt = db.prepareStatement(
						"SELECT firstname, lastname, email "
						+ "FROM patron "
						+ "WHERE firstname=? AND lastname=?;");

		// Check if patron already exists
		try {
			stmt.setString(1, firstname);
			stmt.setString(2, lastname);
			rs = db.executeStatement(stmt);
			if (rs.next()) {
				System.out.println("Patron already exists");
				return false;
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("An error occured while validating patron name");
		}

		// Patron does not exist
		stmt = db.prepareStatement("INSERT INTO patron VALUES (?, ?, ?, 0, ?);");
		try {
			stmt.setInt(1, patronId);
			stmt.setString(2, firstname);
			stmt.setString(3, lastname);
			stmt.setString(4, email);
		} catch (SQLException e) {
		}

		try {
			db.executeUpdateStatement(stmt);
			System.out.println("Successfully added " + lastname + ", " + firstname);
			return true;
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("An error occured while adding patron");
			return false;
		}
	}

	/**
	 * Inserts a new book into the database.
	 * 
	 * @param book
	 *            The book to be inserted
	 * @param firstname
	 *            The firstname of the book's author
	 * @param lastname
	 *            The lastname of the book's author
	 * @return A {@code ResultSet} object containing all books including the
	 *         newly inserted book. Null will be returned if the book could not
	 *         be inserted.
	 */
	public ResultSet newBook(Book book, String firstname, String lastname) {

		ResultSet rs = null;
		PreparedStatement stmt = db.prepareStatement("SELECT isbn FROM book WHERE isbn=?;");

		// Check ISBN
		int isbn = book.getIsbn();
		try {
			stmt.setInt(1, book.getIsbn());
			rs = db.executeStatement(stmt);

			if (rs.next()) {
				System.out.println("ISBN already exists");
				return null;
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("An error occured while validating ISBN");
		}

		// Check author
		boolean aExists = false;
		int authorId = -1;
		try {
			rs = db.executeStatement("SELECT author_id, firstname, lastname FROM author;");

			while (rs.next()) {
				authorId = rs.getInt(1) + 1;
				if (rs.getString(2).equalsIgnoreCase(firstname) && rs.getString(3).equalsIgnoreCase(lastname)) {
					aExists = true;
					authorId = rs.getInt(1);
					break;
				}
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("An error occured while checking authors");
		}

		// Check genre
		String genre = book.getGenre();
		int genreId = -1;

		try {
			rs = db.executeStatement("SELECT * FROM genre;");

			while (rs.next()) {
				if (rs.getString(2).equalsIgnoreCase(genre)) {
					genreId = rs.getInt(1);
					genre = rs.getString(2);
					break;
				}
			}
		} catch (SQLException e) {
			System.out.println("An error occured while checking genres");
		}

		// Genre does not exist
		if (genreId == -1) {
			try {
				rs = db.executeStatement("SELECT COUNT(genre_id) FROM genre;");
				rs.next();
				genreId = rs.getInt(1) + 1;
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
				System.out.println("An error occured");
			}

			stmt = db.prepareStatement("INSERT INTO genre VALUES (?, ?);");
			try {
				stmt.setInt(1, genreId);
				stmt.setString(2, genre);
			} catch (SQLException e) {
			}

			// Add to database
			try {
				db.executeUpdateStatement(stmt);
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
				System.out.println("An error occured while adding the author");
			}
		}

		// Author does not exist
		if (!aExists) {
			stmt = db.prepareStatement("INSERT INTO author VALUES (?, ?, ?);");

			try {
				stmt.setInt(1, authorId);
				stmt.setString(2, firstname);
				stmt.setString(3, lastname);
			} catch (SQLException e) {
			}

			// Add to database
			try {
				db.executeUpdateStatement(stmt);
				System.out.println("Successfully added " + firstname + " " + lastname);
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
				System.out.println("An error occured while adding the author");
			}
		}

		Date date = book.getPubDate();

		// Insert book
		stmt = db.prepareStatement("INSERT INTO book VALUES (?, ?, ?, ?);");

		try {
			stmt.setInt(1, isbn);
			stmt.setString(2, book.getBookTitle());
			if (date == null) {
				stmt.setObject(3, null);
			} else {
				stmt.setDate(3, date);
			}
			stmt.setInt(4, genreId);
		} catch (SQLException e) { // Should never happen
			System.out.println("An error occurred while updating statement");
		}
		// Add to database
		try {
			db.executeUpdateStatement(stmt);
			System.out.println("Successfully added " + book.getBookTitle());
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("An error occured while adding the book");
		}

		// Insert book author
		stmt = db.prepareStatement("INSERT INTO book_authors VALUES (?, ?);");

		try {
			stmt.setInt(1, authorId);
			stmt.setInt(2, isbn);
		} catch (SQLException e) { // Should never happen
			System.out.println("An error occurred while updating statement");
		}
		// Add to database
		try {
			db.executeUpdateStatement(stmt);
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("An error occured while adding the book_author");
		}

		stmt = db.prepareStatement(
				"SELECT isbn AS 'ISBN', book_title AS 'Title', publication_date AS 'Publication Date', genre_name AS 'Genre' "
						+ "FROM book "
						+ "INNER JOIN genre ON genre=genre_id;");
		return db.executeStatement(stmt);
	}

	/**
	 * Will loan a book out to a patron. If the book or patron does not exist,
	 * an SQL syntax Exception will be shown
	 * 
	 * @param book
	 *            The {@code Book} to be loaned out
	 * @param patron
	 *            The {@code Patron} to whom the {@code Book} will be loaned
	 * @return True if the loan was successfully added. False otherwise
	 */
	public boolean loan(Book book, Patron patron) {
		int loanId = -1;

		// Get loan id
		try {
			ResultSet rs = db.executeStatement("SELECT COUNT(loan_id) FROM book_loan;");
			rs.next();
			loanId = rs.getInt(1) + 1;
			rs.close();
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("An error occured while fetching the loans");
		}

		Date date = Date.valueOf(LocalDate.now().plusWeeks(2));

		PreparedStatement stmt = db.prepareStatement("INSERT INTO book_loan VALUES (?, ?, ?, ?, 0);");

		try {
			stmt.setInt(1, loanId);
			stmt.setInt(2, patron.getPatronId());
			stmt.setInt(3, book.getIsbn());
			stmt.setDate(4, date);
		} catch (SQLException e) {
		}

		try {
			db.executeUpdateStatement(stmt);
			System.out.println("Successfully added loan");
			return true;
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("An error occured while executing statement");
			return false;
		}
	}

	/**
	 * Returns the {@code Book}. If the {@code Book} is overdue, a fee will be
	 * charged. Note the fee does not account for leap years.
	 * 
	 * @param book
	 *            The {@code Book} that has been returned
	 * @return True if the book was successfully returned and the fees where
	 *         successfully applied. False otherwise
	 */
	public boolean returnBook(Book book) {

		PreparedStatement stmt = db.prepareStatement(
				"UPDATE book_loan SET returned=1 "
				+ "WHERE book=? AND returned=0;");
		
		try {
			stmt.setInt(1, book.getIsbn());
			db.executeUpdateStatement(stmt);
			System.out.println("Successfully returned book");
		} catch (SQLException e) {
			System.out.println("An error occured while returning book");
			return false;
		}

		int fees = assessFees(book);
		int id = 0;

		ResultSet rs = null;
		stmt = db.prepareStatement("SELECT * FROM book_loan WHERE book=?;");

		try {
			stmt.setInt(1, book.getIsbn());
			rs = db.executeStatement(stmt);
			rs.next();

			id = rs.getInt(1);
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("An error occured while fetching the Patron ID");
			return false;
		}

		Patron p = new Patron(id, null, null, null);
		return setFee(p, fees);
	}

	/**
	 * Renews all books for a given {@Patron}
	 * 
	 * @param patron
	 *            The {@code Patron} whose books are to be renewed For full
	 *            marks, carry out these updates by using a scrolling, updatable
	 *            resultset.
	 * @return True if the patrons books were renewed. False otherwise
	 */
	public boolean renewBooks(Patron patron) {

		ResultSet rs = null;
		PreparedStatement stmt = db.prepareStatement(
				"SELECT * "
				+ "FROM book_loan "
				+ "WHERE returned=0 AND patron_id=?;");

		try {
			stmt.setInt(1, patron.getPatronId());
			rs = db.executeStatement(stmt);
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("An error occurred while executing statement");
			return false;
		}

		try {
			if (rs == null || !rs.next()) {
				return false;
			}
			
			rs.beforeFirst();
			while (rs.next()) {
				// Get book
				Book book = null;
				try {
					book = new Book(rs.getInt(3), null, null, null);
				} catch (SQLException e) {
				}
				
				// Set fees
				int fees = assessFees(book);
				setFee(patron, fees);

				// Set return date
				rs.updateDate(4, Date.valueOf(LocalDate.now().plusWeeks(2)));

				// Finalize
				rs.updateRow();
			}
			return true;
			
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("An error occured while renewing the books");
			return false;
		}
	}

	/**
	 * Gets books based on the patron's previous loans
	 * 
	 * @param patron
	 *            The patron for whom the recommendation is to be done
	 * @return A {@code ResultSet} with the recommended books
	 */
	public ResultSet recommendBook(Patron patron) {
		if (patron == null) {
			return null;
		}

		ResultSet rs = null;

		PreparedStatement stmt = db.prepareStatement(
				"SELECT DISTINCT isbn AS 'ISBN', book_title AS 'Title', publication_date AS 'Publication Date', genre_name AS 'Genre', returned "
						+ "FROM book "
						+ "INNER JOIN genre ON genre=genre_id "
						+ "LEFT OUTER JOIN book_loan ON isbn=book "
						+ "WHERE genre = "
						+ "(SELECT genre FROM book "
						+ "INNER JOIN book_loan ON isbn=book "
						+ "INNER JOIN patron ON patron.patron_id=book_loan.patron_id "
						+ "WHERE patron.patron_id = ? "
						+ "GROUP BY genre "
						+ "ORDER BY COUNT(genre) DESC) "
						+ "HAVING returned IN (1) OR returned IS NULL;");

		try {
			stmt.setInt(1, patron.getPatronId());
			rs = db.executeStatement(stmt);
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("An error occured while fetching the patrons books");
		}

		return rs;
	}

	private int assessFees(Book book) {

		int fees = 0;

		LocalDate dueDate = null;
		LocalDate now = LocalDate.now();

		// Check date overdue
		PreparedStatement stmt = db.prepareStatement("SELECT due_date FROM book_loan WHERE book=?;");
		ResultSet rs = null;

		try {
			stmt.setInt(1, book.getIsbn());
			rs = db.executeStatement(stmt);
			rs.next();
			dueDate = rs.getDate(1).toLocalDate();
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("An error occured while fetching the due date");
		}

		if (dueDate.isBefore(now)) {
			int difference = 0;

			int yearDiff = now.getYear() - dueDate.getYear();
			int dayDiff = now.getDayOfYear() - dueDate.getDayOfYear();

			if (yearDiff > 0) {
				difference += yearDiff * 365;
			}

			// Get difference in days
			difference += dayDiff;

			// Get fee
			fees += difference * 5;
		}

		return fees;
	}

	private boolean setFee(Patron patron, int fees) {
		fees += patron.getFees();

		// Update fee
		PreparedStatement stmt = db.prepareStatement("UPDATE patron SET fees=? WHERE patron_id=?;");
		try {
			stmt.setInt(1, fees);
			stmt.setInt(2, patron.getPatronId());

			db.executeUpdateStatement(stmt);

			System.out.println("Successfully added fee of " + fees);
			return true;
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("An error occured while setting fees");
			return false;
		}
	}
}
