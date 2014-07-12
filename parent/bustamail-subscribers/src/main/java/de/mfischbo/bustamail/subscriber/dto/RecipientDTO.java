package de.mfischbo.bustamail.subscriber.dto;

import java.util.UUID;

import de.mfischbo.bustamail.common.domain.Gender;
import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;

public class RecipientDTO implements PersonalizedEmailRecipient {

	private UUID			id;
	private String			firstName;
	private String			lastName;
	private Gender			gender;
	private	String		 	email;
	private boolean			formalSalutation;

	public UUID getId() {
		return id;
	}
	public void setSubscriberId(UUID id) {
		this.id = id;
	}
	
	@Override
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@Override
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@Override
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	
	@Override
	public String getEmail() {
		return email;
	}
	public void setAddress(EMailAddress address) {
		this.email = address.getLocalPart() + "@" + address.getDomainPart();
	}
	public void setEmailAddress(String email) {
		this.email = email;
	}

	@Override
	public boolean hasFormalSalutation() {
		return formalSalutation;
	}
	public void setFormalSalutation(boolean formalSalutation) {
		this.formalSalutation = formalSalutation;
	}
	
	
}
