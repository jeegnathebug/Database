package application;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import business.Book;
import business.Patron;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.*;
import javafx.util.Callback;

public class MainController extends Library {

	public AnchorPane paneMain;
	public MenuBar menuBar;
	public Menu menuFile;
	public Menu menuTheme;
	public ToggleGroup radioButtonsTheme;
	public RadioMenuItem menuItemLight;
	public RadioMenuItem menuItemDark;
	public MenuItem menuItemDisconnect;
	public MenuItem menuItemClose;
	public Menu menuEdit;
	public MenuItem menuItemClear;
	public Menu menuHelp;
	public MenuItem menuItemAbout;

	public GridPane paneChoice;
	public Button buttonGetBook;
	public Button buttonBookReport;
	public Button buttonNewPatron;
	public Button buttonNewBook;
	public Button buttonLoan;
	public Button buttonReturn;
	public Button buttonRenew;
	public Button buttonRecommend;
	public Button buttonNewUser;
	public Button buttonLogin;

	public AnchorPane paneGetBook;
	public TextField textFieldIsbnGetBook;
	public Button buttonEnterGetBook;

	public AnchorPane paneBookReport;
	public Button buttonEnterBookReport;

	public AnchorPane paneNewPatron;
	public TextField textFieldFirstnameNewPatron;
	public TextField textFieldLastnameNewPatron;
	public TextField textFieldEmail;
	public Button buttonEnterNewPatron;

	public AnchorPane paneNewBook;
	public TextField textFieldIsbnNewBook;
	public TextField textFieldTitle;
	public TextField textFieldGenre;
	public TextField textFieldFirstnameNewBook;
	public TextField textFieldLastnameNewBook;
	public DatePicker textFieldDate;
	public Button buttonEnterNewBook;

	public AnchorPane paneLoan;
	public TextField textFieldIsbnLoan;
	public TextField textFieldPatronIdLoan;
	public Button buttonEnterLoan;

	public AnchorPane paneReturn;
	public TextField textFieldIsbnReturn;
	public Button buttonEnterReturn;

	public AnchorPane paneRenew;
	public TextField textFieldPatronIdRenew;
	public Button buttonEnterRenew;

	public AnchorPane paneRecommend;
	public TextField textFieldPatronIdRecommend;
	public Button buttonEnterRecommend;

	public AnchorPane paneNewUser;
	public TextField textFieldUsernameNewUser;
	public TextField textFieldPasswordNewUser;
	public Button buttonEnterNewUser;

	public AnchorPane paneLogin;
	public TextField textFieldUsernameLogin;
	public TextField textFieldPasswordLogin;
	public Button buttonEnterLogin;

	public Button buttonExit;
	public Button buttonBack;

	public Label labelStatus;

	public TableView<ObservableList<String>> table;

	public void back() {
		if (paneChoice.isVisible()) {
			disconnect();
		} else {
			paneGetBook.setVisible(false);
			paneBookReport.setVisible(false);
			paneNewPatron.setVisible(false);
			paneNewBook.setVisible(false);
			paneLoan.setVisible(false);
			paneReturn.setVisible(false);
			paneRenew.setVisible(false);
			paneRecommend.setVisible(false);
			paneNewUser.setVisible(false);
			paneLogin.setVisible(false);

			paneChoice.setVisible(true);

			buttonBack.setText("Disconnect");
		}
	}

	public void choice(Event event) {
		String pane = "#pane" + ((Button) event.getSource()).getId().substring(6);
		((AnchorPane) scene.lookup(pane)).setVisible(true);
		paneChoice.setVisible(false);
		buttonBack.setText("Back");
	}

	public void enterGetBook() {
		labelStatus.setText(null);
		table.getColumns().clear();

		int isbn;
		try {
			isbn = Integer.parseInt(textFieldIsbnGetBook.getText());
		} catch (NumberFormatException e) {
			return;
		}

		try {
			ResultSet rs = ldb.getBook(isbn);
			if (!rs.next()) {
				labelStatus.setText("No books found with ISBN of " + isbn);
			} else {
				rs.beforeFirst();
				displayResults(rs);
			}
		} catch (SQLException e) {
			labelStatus.setText("An error occurred");
		}
	}

	public void enterBookReport() {
		labelStatus.setText(null);
		table.getColumns().clear();

		try {
			ResultSet rs = ldb.bookReport();
			if (!rs.next()) {
				labelStatus.setText("No books in database");
			} else {
				rs.beforeFirst();
				displayResults(rs);
			}
		} catch (SQLException e) {
			labelStatus.setText("An error occurred");
		}
	}

	public void enterNewPatron() {
		labelStatus.setText(null);
		table.getColumns().clear();

		// Get info
		String firstname = textFieldFirstnameNewPatron.getText().trim();
		String lastname = textFieldLastnameNewPatron.getText().trim();
		if (firstname.equals("") || lastname.equals("")) {
			return;
		}
		String email = textFieldEmail.getText();
		if (email == null || email.trim().equals("")) {
			email = "";
		}

		try {
			if (!ldb.newPatron(firstname, lastname, email)) {
				labelStatus.setText("Patron could not be added to the database");
			} else {
				displayResults(db.executeStatement(
						"SELECT firstname AS 'Firstname', lastname AS 'Lastname', fees AS 'Fees', email AS 'Email'"
								+ "FROM patron;"));
			}
		} catch (SQLException e) {
			labelStatus.setText("An error occurred");
		}
	}

	public void enterNewBook() {
		labelStatus.setText(null);
		table.getColumns().clear();

		// Get isbn
		int isbn;
		try {
			isbn = Integer.parseInt(textFieldIsbnNewBook.getText().trim());
		} catch (NumberFormatException e) {
			return;
		}

		// Get title
		String title = textFieldTitle.getText().trim();
		if (title.equals("")) {
			return;
		}

		// Get pub date
		LocalDate date = textFieldDate.getValue();
		Date pubDate = null;
		if (date != null) {
			pubDate = Date.valueOf(textFieldDate.getValue());
		}

		// Get first name
		String firstname = textFieldFirstnameNewBook.getText().trim();
		if (firstname.equals("")) {
			return;
		}

		// Get last name
		String lastname = textFieldLastnameNewBook.getText().trim();
		if (lastname.equals("")) {
			return;
		}

		// Get genre
		String genre = textFieldGenre.getText().trim();
		if (genre.equals("")) {
			return;
		}

		try {
			ResultSet rs = ldb.newBook(new Book(isbn, title, pubDate, genre), firstname, lastname);
			if (rs == null || !rs.next()) {
				labelStatus.setText("Could not add book");
			} else {
				rs.beforeFirst();
				displayResults(rs);
			}
		} catch (SQLException e) {
			labelStatus.setText("An error occurred");
		}
	}

	public void enterLoan() {
		labelStatus.setText(null);
		table.getColumns().clear();

		// Get information
		int isbn;
		try {
			isbn = Integer.parseInt(textFieldIsbnLoan.getText());
		} catch (NumberFormatException e) {
			return;
		}

		int patronId;
		try {
			patronId = Integer.parseInt(textFieldPatronIdLoan.getText());
		} catch (NumberFormatException e) {
			return;
		}

		// Get book
		Book book = null;
		ResultSet rs = db.executeStatement("SELECT isbn, book_title, publication_date, genre_name "
				+ "FROM book INNER JOIN genre ON genre=genre_id " + "WHERE isbn=" + isbn + ";");
		try {
			rs.next();
			book = new Book(isbn, rs.getString(2), rs.getDate(3), rs.getString(4));
		} catch (SQLException e) {
			labelStatus.setText("An error occurred while getting Book");
			return;
		}

		// Get patron
		Patron patron = null;
		rs = db.executeStatement("SELECT * FROM patron WHERE patron_id=" + patronId + ";");
		try {
			rs.next();

			patron = new Patron(patronId, rs.getString(2), rs.getString(3), rs.getString(5));
			patron.setFees(rs.getInt(4));
		} catch (SQLException e) {
			labelStatus.setText("An error occurred while getting Patron");
			return;
		}

		// Loan book
		if (ldb.loan(book, patron)) {
			labelStatus.setText(book.getBookTitle() + " successfully loaned to " + patron.getFirstname() + " "
					+ patron.getLastname());
		} else {
			labelStatus.setText("Could not add loan");
		}
	}

	public void enterReturn() {
		labelStatus.setText(null);
		table.getColumns().clear();

		// Get information
		int isbn;
		try {
			isbn = Integer.parseInt(textFieldIsbnReturn.getText());
		} catch (NumberFormatException e) {
			return;
		}

		ResultSet rs = db.executeStatement("SELECT isbn, book_title, publication_date, genre_name "
				+ "FROM book INNER JOIN genre ON genre=genre_id " + "WHERE isbn=" + isbn + ";");

		try {
			rs.next();
			// Get book information
			Book book = new Book(isbn, rs.getString(2), rs.getDate(3), rs.getString(4));

			// Return book
			if (ldb.returnBook(book)) {
				labelStatus.setText("Successfully returned " + book.getBookTitle());
			} else {
				labelStatus.setText("Could not return book");
			}
		} catch (SQLException e) {
			labelStatus.setText("An error occurred while getting book information");
		}
	}

	public void enterRenew() {
		labelStatus.setText(null);
		table.getColumns().clear();

		// Get information
		int patronId;
		try {
			patronId = Integer.parseInt(textFieldPatronIdRenew.getText());
		} catch (NumberFormatException e) {
			return;
		}

		ResultSet rs = db.executeStatement("SELECT * FROM patron WHERE patron_id=" + patronId + ";");
		try {
			rs.next();
			// Get patron information
			Patron patron = new Patron(patronId, rs.getString(2), rs.getString(3), rs.getString(5));

			// Set fees
			patron.setFees(rs.getInt(4));

			// Renew books
			if (ldb.renewBooks(patron)) {
				labelStatus.setText(
						"Successfully renewed all books of " + patron.getFirstname() + " " + patron.getLastname());
			} else {
				labelStatus.setText("Could not renew books");
			}
		} catch (SQLException e) {
			labelStatus.setText("An error occurred while getting Patron");
		}
	}

	public void enterRecommend() {
		labelStatus.setText(null);
		table.getColumns().clear();

		// Get information
		int patronId;
		try {
			patronId = Integer.parseInt(textFieldPatronIdRecommend.getText());
		} catch (NumberFormatException e) {
			return;
		}

		Patron patron = null;
		ResultSet rs = db.executeStatement("SELECT * FROM patron WHERE patron_id=" + patronId + ";");
		try {
			rs.next();
			// Get patron information
			patron = new Patron(patronId, rs.getString(2), rs.getString(3), rs.getString(5));
			patron.setFees(rs.getInt(4));
		} catch (SQLException e) {
			labelStatus.setText("An error occurred while getting Patron");
			return;
		}

		try {
			rs = ldb.recommendBook(patron);
			if (rs == null || !rs.next()) {
				labelStatus.setText("No books to display");
			} else {
				displayResults(rs);
				table.getColumns().remove(4);
			}
		} catch (SQLException e) {
			labelStatus.setText("An error occurred");
		}
	}

	public void enterNewUser() {
		labelStatus.setText(null);
		table.getColumns().clear();

		// Get information
		String username = textFieldUsernameNewUser.getText().trim();
		String password = textFieldPasswordNewUser.getText().trim();
		if (username.equals("") || password.equals("")) {
			return;
		}

		if (ls.newUser(username, password)) {
			labelStatus.setText(username + " created successfully");
		} else {
			labelStatus.setText("Could not create user " + username);
		}
	}

	public void enterLogin() {
		labelStatus.setText(null);
		table.getColumns().clear();

		// Get information
		String username = textFieldUsernameLogin.getText().trim();
		String password = textFieldPasswordLogin.getText().trim();
		if (username.equals("") || password.equals("")) {
			return;
		}

		if (ls.login(username, password)) {
			labelStatus.setText("Welcome " + username);
		} else {
			labelStatus.setText("Username or password is incorrect");
		}
	}

	public void exit() {
		close();
		System.out.println("Bye!");
		System.exit(0);
	}

	public void clear() {
		// getBook
		textFieldIsbnGetBook.setText(null);
		// newPatron
		textFieldFirstnameNewPatron.setText(null);
		textFieldLastnameNewPatron.setText(null);
		textFieldEmail.setText(null);
		// newBook
		textFieldIsbnNewBook.setText(null);
		textFieldTitle.setText(null);
		textFieldGenre.setText(null);
		textFieldFirstnameNewBook.setText(null);
		textFieldLastnameNewBook.setText(null);
		textFieldDate.setValue(null);
		// loan
		textFieldIsbnLoan.setText(null);
		textFieldPatronIdLoan.setText(null);
		// return
		textFieldIsbnReturn.setText(null);
		// renew
		textFieldPatronIdRenew.setText(null);
		// recommend
		textFieldPatronIdRecommend.setText(null);
		// newUser
		textFieldUsernameNewUser.setText(null);
		textFieldPasswordNewUser.setText(null);
		// checkUser
		textFieldUsernameLogin.setText(null);
		textFieldPasswordLogin.setText(null);

		// table
		table.getColumns().clear();
	}

	public void disconnect() {
		close();
		// Next screen
		next(false, "Login.fxml", style);
	}

	public void themeDark() {
		style = "dark.css";
		setStylesheet(style);
	}

	public void themeLight() {
		style = "light.css";
		setStylesheet(style);
	}

	private void close() {
		try {
			labelStatus.setText("Closing connection...");
			db.closeConnection();
			labelStatus.setText("Connection closed successfully");
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			labelStatus.setText("Connection could not be closed successfully");
		}
	}

	private void displayResults(ResultSet rs) throws SQLException {

		if (rs == null) {
			return;
		}

		table.getColumns().clear();

		if (rs != null) {

			String name = "ERROR";
			int columnCount = -1;

			// Get column count
			columnCount = rs.getMetaData().getColumnCount();

			// Create columns
			TableColumn<ObservableList<String>, String> col = null;

			for (int i = 1; i <= columnCount; i++) {
				final int j = i - 1;

				// Get column name
				name = rs.getMetaData().getColumnLabel(i);

				// Create column
				col = new TableColumn<ObservableList<String>, String>(name);
				col.setCellValueFactory(
						new Callback<CellDataFeatures<ObservableList<String>, String>, ObservableValue<String>>() {
							public ObservableValue<String> call(
									CellDataFeatures<ObservableList<String>, String> param) {
								return new SimpleStringProperty(param.getValue().get(j));
							}
						});

				// Add column
				table.getColumns().add(col);
			}

			// Get info from ResultSet
			ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

			while (rs.next()) {

				// Iterate Row
				ObservableList<String> row = FXCollections.observableArrayList();

				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					// Iterate Column
					try {
						row.add(rs.getString(i));
					} catch (NullPointerException e) {
					}
				}
				data.add(row);
			}
			table.setItems(data);
		}
		
		// Close ResultSet
		rs.close();
	}
}
