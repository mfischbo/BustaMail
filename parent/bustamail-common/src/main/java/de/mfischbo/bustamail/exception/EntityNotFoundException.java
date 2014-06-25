package de.mfischbo.bustamail.exception;

public class EntityNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8638113746053370384L;
	
	private String message;
	
	public EntityNotFoundException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}

}
