package de.mfischbo.bustamail.common.domain;

import java.util.UUID;

public interface PersonalizedEmailRecipient {

	UUID	getId();
	
	Gender	getGender();
	
	String	getFirstName();
	
	String	getLastName();
	
	boolean	hasFormalSalutation();
	
	String	getEmail();
}
