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

	public AnchorPane paneQuery;

	public GridPane paneChoice;
	public Button buttonGetBook;
	public Button buttonBookReport;
	public Button buttonNewPatron;
	public Button buttonNewBook;
	public Button buttonLoan;
	public Button buttonReturn;
	public Button buttonRenew;
	public Button buttonRecommend;

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

	public Button buttonExit;
	public Button buttonBack;
	public Button buttonDisconnect;

	public TableView<ObservableList<String>> table;

	public void back() {
		paneGetBook.setVisible(false);
		paneBookReport.setVisible(false);
		paneNewPatron.setVisible(false);
		paneNewBook.setVisible(false);
		paneLoan.setVisible(false);
		paneReturn.setVisible(false);
		paneRenew.setVisible(false);
		paneRecommend.setVisible(false);

		paneChoice.setVisible(true);
	}

	public void choiceGetBook() {
		paneGetBook.setVisible(true);
		paneChoice.setVisible(false);
	}

	public void choiceBookReport() {
		paneBookReport.setVisible(true);
		paneChoice.setVisible(false);
	}

	public void choiceNewPatron() {
		paneNewPatron.setVisible(true);
		paneChoice.setVisible(false);
	}

	public void choiceNewBook() {
		paneNewBook.setVisible(true);
		paneChoice.setVisible(false);
	}

	public void choiceLoan() {
		paneLoan.setVisible(true);
		paneChoice.setVisible(false);
	}

	public void choiceReturn() {
		paneReturn.setVisible(true);
		paneChoice.setVisible(false);
	}

	public void choiceRenew() {
		paneRenew.setVisible(true);
		paneChoice.setVisible(false);
	}

	public void choiceRecommend() {
		paneRecommend.setVisible(true);
		paneChoice.setVisible(false);
	}

	public void enterGetBook() {
		int isbn = Integer.parseInt(textFieldIsbnGetBook.getText());

		try {
			displayResults(ldb.getBook(isbn));
		} catch (SQLException e) {
			System.out.println("An error occured");
		}
	}

	public void enterBookReport() {
		try {
			displayResults(ldb.bookReport());
		} catch (SQLException e) {
			System.out.println("An error occured :c");
		}
	}

	public void enterNewPatron() {
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
			displayResults(ldb.newPatron(firstname, lastname, email));
		} catch (SQLException e) {
			System.out.println("An error occured");
		}
	}

	public void enterNewBook() {
		// Get isbn
		int isbn = -1;
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

		// Get firstname
		String firstname = textFieldFirstnameNewBook.getText().trim();
		if (firstname.equals("")) {
			return;
		}

		// Get lastname
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
			displayResults(ldb.newBook(new Book(isbn, title, pubDate, genre), firstname, lastname));
		} catch (SQLException e) {
			System.out.println("An error occured");
		}
	}

	public void enterLoan() {
		int isbn = Integer.parseInt(textFieldIsbnLoan.getText());
		int patronId = Integer.parseInt(textFieldPatronIdLoan.getText());

		ldb.loan(new Book(isbn, null, null, null), new Patron(patronId, null, null, null));
	}

	public void enterReturn() {
		int isbn = Integer.parseInt(textFieldIsbnReturn.getText());
		ldb.returnBook(new Book(isbn, null, null, null));
	}

	public void enterRenew() {
		int patronId = Integer.parseInt(textFieldPatronIdRenew.getText());
		ldb.renewBooks(new Patron(patronId, null, null, null));
	}

	public void enterRecommend() {
		int patronId = Integer.parseInt(textFieldPatronIdRecommend.getText());
		try {
			displayResults(ldb.recommendBook(new Patron(patronId, null, null, null)));
		} catch (SQLException e) {
			System.out.println("An error occured");
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
		textFieldDate.setValue(LocalDate.now());
		// loan
		textFieldIsbnLoan.setText(null);
		textFieldPatronIdLoan.setText(null);
		// return
		textFieldIsbnReturn.setText(null);
		// renew
		textFieldPatronIdRenew.setText(null);
		// recommend
		textFieldPatronIdRecommend.setText(null);

		// table
		table.getColumns().clear();
	}

	public void disconnect() {
		close();

		// Next screen
		next(false, "Login.fxml", "application.css");
	}

	private void close() {
		db.closeConnection();
	}

	private void displayResults(ResultSet rs) throws SQLException {

		if (rs == null) {
			return;
		}
		
		int size = table.getColumns().size();

		// clear previous table
		if (size != 0) {
			table.getColumns().clear();
		}

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
						new Callback<CellDataFeatures<ObservableList<String>, String>, ObservableValue<String>>()
							{
							public ObservableValue<String> call(CellDataFeatures<ObservableList<String>, String> param) {
								if (param.getValue().get(j) != null){
								return new SimpleStringProperty(param.getValue().get(j).toString());
								} else {
									return new SimpleStringProperty("Null");
								}
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
				//System.out.println(row);

				data.add(row);
			}

			table.setItems(data);
		}
	}
}
