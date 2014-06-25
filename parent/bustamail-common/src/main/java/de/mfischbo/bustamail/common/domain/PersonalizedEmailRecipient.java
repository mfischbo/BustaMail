package de.mfischbo.bustamail.common.domain;

public interface PersonalizedEmailRecipient {

	Gender	getGender();
	
	String	getFirstName();
	
	String	getLastName();
	
	boolean	hasFormalSalutation();
	
	String	getEmail();
}
