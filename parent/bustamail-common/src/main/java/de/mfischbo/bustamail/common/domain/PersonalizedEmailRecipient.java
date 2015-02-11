package de.mfischbo.bustamail.common.domain;

import org.bson.types.ObjectId;

public interface PersonalizedEmailRecipient {

	ObjectId getId();
	
	Gender	getGender();
	
	String	getFirstName();
	
	String	getLastName();
	
	String  getTitle();
	
	boolean	hasFormalSalutation();
	
	String	getEmail();
}
