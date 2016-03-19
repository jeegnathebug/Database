package business;

import java.sql.Date;
import java.util.Optional;

public class Book {

	private int isbn;
	private String bookTitle;
	private Optional<Date> pubDate;
	private String genre;

	public Book(int isbn, String bookTitle, Date pubDate, String genre) {
		this.isbn = isbn;
		this.bookTitle = bookTitle;
		this.pubDate = Optional.ofNullable(pubDate);
		this.genre = genre;
	}

	public int getIsbn() {
		return isbn;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public Date getPubDate() {
		return pubDate.orElse(null);
	}

	public String getGenre() {
		return genre;
	}

	public void setIsbn(int isbn) {
		this.isbn = isbn;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = Optional.ofNullable(pubDate);
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	@Override
	public String toString() {
		return isbn + ", " + bookTitle + (pubDate.isPresent() ? ", " + pubDate.get() : "") + ", " + genre;
	}
}
