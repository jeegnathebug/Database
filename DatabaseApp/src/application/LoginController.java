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

	public Label labelStatus;

	public void connect() {
		Optional<String> url = Optional.ofNullable(textFieldURL.getText());
		String username = textFieldUsername.getText();
		String password = textFieldPassword.getText();

		// Create connection
		getConnection(url.orElse("localhost:3306"), username, password);
		// Next screen
		next(true, "Main.fxml", "light.css");
	}

	public void cancel() {
		System.out.println("Bye!");
		System.exit(0);
	}

	private void getConnection(String url, String username, String password) {
		try {
			labelStatus.setText("Creating connection...");
			db = new DatabaseConnector(url, username, password);
			labelStatus.setText("Connection created successfully");
		} catch (SQLException e) {
			labelStatus.setText("An error occured while creating connection");
		}
	}

	public void enableConnect() {
		if (!(textFieldUsername.getText() == null || textFieldUsername.getText().trim().equals(""))) {
			buttonConnect.setDisable(false);
		}
	}
}
