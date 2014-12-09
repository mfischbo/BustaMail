package de.mfischbo.bustamail.landingpage.dto;

public class ValidationError {

	public enum ErrorType {
		MISSING_REQUIRED
	}
	
	private String fieldname;
	private ErrorType	type;
	
	public ValidationError(String fieldname, ErrorType type) {
		this.fieldname = fieldname;
		this.type = type;
	}

	public String getFieldname() {
		return fieldname;
	}

	public ErrorType getType() {
		return type;
	}
}
