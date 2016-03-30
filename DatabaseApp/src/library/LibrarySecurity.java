package library;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.math.BigInteger;
import java.sql.*;
import java.util.Arrays;

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
	 * @return True if the user was added to the database. False otherwise
	 */
	public boolean newUser(String username, String password) {
		if (username == null || username.trim().equals("") || password == null || password.trim().equals("")) {
			System.out.println("Username or password cannot be null");
			return false;
		}

		// User does not exist
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
				return true;
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
				System.out.println("An error occured while updating table");
				return false;
			}
		}

		return false;
	}

	/**
	 * Checks whether an account exists for the given user name
	 * 
	 * @param username
	 *            The user name to be checked
	 * @param password
	 *            The password of the user
	 * @return True if the user name belongs to a valid user. False otherwise
	 */
	public boolean login(String username, String password) {
		if (username == null || username.trim().equals("") || password == null || password.trim().equals("")) {
			return false;
		}

		// Create statement
		PreparedStatement stmt = db.prepareStatement("SELECT * FROM users WHERE userid=?;");
		try {
			stmt.setString(1, username);
		} catch (SQLException e) {
		}

		// Execute statement
		ResultSet rs = db.executeStatement(stmt);

		// Get salt
		String salt;
		try {
			rs.next();
			salt = rs.getString(2);
		} catch (SQLException e) {
			System.out.println("Could not retrieve salt");
			return false;
		}

		// Check for existing user name
		try {
			// Get hashes
			byte[] dbHash = rs.getBytes(3);
			byte[] inputHash = hash(password, salt);
			boolean equal;

			// Check equality of hashes
			equal = Arrays.equals(dbHash, inputHash);

			return equal;
		} catch (SQLException e) {
			System.out.println("An error occured while checking current users");
		}

		return false;
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
