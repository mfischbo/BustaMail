package de.mfischbo.bustamail.mailinglist.validation;


public interface Validator {

	public enum ResultType {
		UNPARSEABLE_EMAIL_ADDRESS,		// if the email address is not parseable
		UNKNOWN_GENDER,					// if the gender is not M or F
		INVALID_BOOLEAN_EXPRESSION, 	// if a boolean expression could not be recognized
		INVALID_COUNTRY_CODE,			// if a country code is not valid
		SUCCESS							// everything's ok
	}
	
	public ResultType validate(String value);
}
