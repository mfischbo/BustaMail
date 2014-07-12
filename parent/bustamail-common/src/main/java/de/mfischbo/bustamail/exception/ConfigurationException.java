package de.mfischbo.bustamail.exception;

public class ConfigurationException extends Exception {

	private static final long serialVersionUID = -8554150603044500290L;

	private String	message;
	
	public ConfigurationException(String message) {
		super();
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
