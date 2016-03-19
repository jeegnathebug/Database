package application;

import java.sql.SQLException;
import java.util.Optional;

import connector.DatabaseConnector;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

public class LoginController extends Library {

	public AnchorPane paneLogin;

	public Button buttonConnect;
	public Button buttonCancel;

	public TextField textFieldURL;
	public TextField textFieldUsername;
	public PasswordField textFieldPassword;

	public void connect() {
		Optional<String> url = Optional.ofNullable(textFieldURL.getText());
		String username = textFieldUsername.getText();
		String password = textFieldPassword.getText();

		try {
			// Create connection
			getConnection(url.orElse("localhost:3306"), username, password);
			// Next screen
			next(true, "Main.fxml", "application.css");

		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("An error occured while connecting to database");
		}
	}

	public void cancel() {
		System.out.println("Bye!");
		System.exit(0);
	}

	private void getConnection(String url, String username, String password) throws SQLException {
		db = new DatabaseConnector(url, username, password);
	}

	public void enableConnect() {
		if (!(textFieldUsername.getText() == null || textFieldUsername.getText().trim().equals(""))) {
			buttonConnect.setDisable(false);
		}
	}
}
