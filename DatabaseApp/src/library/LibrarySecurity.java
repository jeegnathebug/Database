package library;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.math.BigInteger;
import java.sql.*;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import application.Library;
import connector.DatabaseConnector;

public class LibrarySecurity extends Library {

	private static SecureRandom random = new SecureRandom();

	private DatabaseConnector db;

	private String dbuser = "<YOURUSERNAME>";
	private String dbname = "<YOURDBNAME>";
	private String dbpassword = "<YOURDBPASSWORD>";

	// Takes a username and password and creates and account for that user
	public void newUser(String username, String password) throws SQLException {

		PreparedStatement stmt = db.prepareStatement("INSERT INTO users VALUES (?, ?, ?);");
		
		String salt = getSalt();
		stmt.setString(1, username);
		stmt.setString(2, salt);
		stmt.setBytes(3, hash(password, salt));
		
		db.executeStatement(stmt);
	}

	// Prompts the user to input a username and password, and creates an account
	// for that user.
	public void newUser() throws SQLException {

	}

	// Takes a username and password returns true if they belong to a valid user
	public boolean login(String username, String password) throws SQLException {
		return false;
	}

	// Prompts the user to input their login info, returns true if they are a
	// valid user, false otherwise
	public boolean login() throws SQLException {
		return false;
	}

	// Creates a randomly generated String
	public String getSalt() {
		return new BigInteger(140, random).toString(32);
	}

	// Takes a password and a salt a performs a one way hashing on them,
	// returning an array of bytes.
	public byte[] hash(String password, String salt) {
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");

			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 1024, 256);

			SecretKey key = skf.generateSecret(spec);
			byte[] hash = key.getEncoded();
			return hash;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}
}
