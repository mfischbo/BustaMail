package de.mfischbo.bustamail.exception;

public class BustaMailException extends Exception {

	private static final long serialVersionUID = 2002501569522747610L;

	private String message;
	
	public BustaMailException(String message) {
		super(message);
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
