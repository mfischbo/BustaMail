package de.mfischbo.bustamail.subscriber.domain;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.common.domain.Gender;

@Document(collection = "Subscribers_Contact")
public class Contact extends BaseDomain {

	private static final long serialVersionUID = -7625104438651224536L;

	private String title;
	
	private Gender gender = Gender.N;
	
	private String firstName;
	
	private String lastName;

	private boolean formalSalutation;

	private List<Address>		addresses;

	private Set<EMailAddress>	emailAddresses;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isFormalSalutation() {
		return formalSalutation;
	}

	public void setFormalSalutation(boolean formalSalutation) {
		this.formalSalutation = formalSalutation;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public Set<EMailAddress> getEmailAddresses() {
		return emailAddresses;
	}

	public void setEmailAddresses(Set<EMailAddress> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}
}
