package de.mfischbo.bustamail.exception;

public class DataIntegrityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4913414573675406290L;
	private String message;
	
	public DataIntegrityException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
