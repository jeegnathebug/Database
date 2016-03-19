package business;

import java.util.Optional;

public class Patron {

	private int patronId;
	private String firstname;
	private String lastname;
	private int fees;
	private Optional<String> email;

	public Patron(int patronId, String firstname, String lastname, String email) {
		this.patronId = patronId;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = Optional.ofNullable(email);
	}

	public int getPatronId() {
		return patronId;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public int getFees() {
		return fees;
	}

	public void setPatronId(int patronId) {
		this.patronId = patronId;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public void setFees(int fees) {
		this.fees = fees;
	}

	/**
	 * May return empty string
	 * 
	 * @return
	 */
	public String getEmail() {
		return email.orElse(null);
	}
}
