package application;

import connector.DatabaseConnector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.*;

public class Library extends Application {

	/**
	 * The primary stage for this application, onto which the application scene
	 * will be set.
	 */
	public static Stage primaryStage;

	private static FXMLLoader loader;
	
	protected static DatabaseConnector db = null;
	protected static LibraryDatabase ldb;
	protected static LibrarySecurity ls;

	public static void main(String[] args) {
		ldb = new LibraryDatabase();
		ls = new LibrarySecurity();
		launch(args);
	}

	/**
	 * Sets the scene of the primary stage to the given resource and applies the
	 * given style sheet to it
	 * 
	 * @param resource
	 *            The fxml document
	 * @param stylesheet
	 *            The style sheet
	 */
	public void setScene(String resource, String stylesheet) {

		loader = new FXMLLoader(getClass().getResource(resource));
		AnchorPane root = null;
		try {
			root = (AnchorPane) loader.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(root);

		// Add stylesheet
		// scene.getStylesheets().add(getClass().getResource(stylesheet).toExternalForm());

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void start(Stage primaryStage) {

		// Set stage
		Library.primaryStage = primaryStage;
		primaryStage.setResizable(false);

		next(false, "Login.fxml", "applicaion.css");
	}

	void next(boolean isLogin, String resource, String stylesheet) {

		// Set width and height
		int height = 175;
		int width = 275;

		if (isLogin) {
			height = 500;
			width = 650;
		}

		primaryStage.setHeight(height);
		primaryStage.setWidth(width);

		primaryStage.setResizable(isLogin);
		
		setScene(resource, stylesheet);
	}
}
