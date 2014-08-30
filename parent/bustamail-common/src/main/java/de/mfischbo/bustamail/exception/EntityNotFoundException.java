package de.mfischbo.bustamail.exception;

public class EntityNotFoundException extends BustaMailException {

	private static final long serialVersionUID = -8638113746053370384L;
	
	public EntityNotFoundException(String message) {
		super(message);
	}
}
