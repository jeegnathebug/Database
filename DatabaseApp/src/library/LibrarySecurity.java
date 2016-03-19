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

public class LibrarySecurity extends Library {

	private static SecureRandom random = new SecureRandom();

	/**
	 * Creates an account for a given user name. If the user name already
	 * exists, or is not a valid user name, nothing will happen. Invalid user
	 * names include empty strings and null values
	 * 
	 * @param username
	 *            The user name
	 * @param password
	 *            The password
	 */
	public void newUser(String username, String password) {

		if (username == null || username.trim().equals("") || password == null || password.trim().equals("")) {
			System.out.println("Username or password cannot be null");
			return;
		}

		if (!login(username, password)) {
			PreparedStatement stmt = db.prepareStatement("INSERT INTO users VALUES (?, ?, ?);");

			String salt = getSalt();
			
			try {
				stmt.setString(1, username);
				stmt.setString(2, salt);
				stmt.setBytes(3, hash(password, salt));
			} catch (SQLException e) {
			}

			try {
				db.executeUpdateStatement(stmt);
				System.out.println("Successfully added user " + username);
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
				System.out.println("An error occured while updating table");
			}
		}
	}

	/**
	 * Checks whether an account exists for the given user name
	 * 
	 * @param username
	 *            The user name to be checked
	 * @param password
	 *            The password of the user
	 * @return True if the user name belongs to a valid user
	 */
	public boolean login(String username, String password) {
		if (username == null || username.trim().equals("")) {
			System.out.println("Username cannot be null");
			return false;
		}

		boolean exists = false;

		// Create statement
		PreparedStatement stmt = db.prepareStatement("SELECT * FROM users WHERE userid=?;");
		try {
			stmt.setString(1, username);
		} catch (SQLException e) {
		}

		// Execute statement
		ResultSet rs = db.executeStatement(stmt);

		// Check for existing user name
		try {
			if (rs.next()) {
				exists = true;
			}
		} catch (SQLException e) {

			System.out.println("An error occured while checking current users");
		}

		return exists;
	}

	/**
	 * Generates a random {@code String}
	 * 
	 * @return The randomly generated {@code String}
	 */
	public String getSalt() {
		return new BigInteger(140, random).toString(32);
	}

	/**
	 * Creates a hash from the given password and salt
	 * 
	 * @param password
	 *            The password to be used in the hash
	 * @param salt
	 *            The salt to be used in the hash
	 * @return The has
	 */
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
