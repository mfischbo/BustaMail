package de.mfischbo.bustamail.subscriber.dto;

import java.util.List;

import de.mfischbo.bustamail.common.domain.Gender;
import de.mfischbo.bustamail.common.dto.BaseDTO;
import de.mfischbo.bustamail.subscriber.domain.Address;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;

public class ContactDTO extends BaseDTO {


	private static final long serialVersionUID = 560150943874391814L;

	private String						title;
	private Gender						gender;
	private String 						firstName;
	private String						lastName;
	private boolean						formalSalutation;
	private List<Address>				addresses;
	private List<EMailAddress>			emailAddresses;
	
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
	public List<EMailAddress> getEmailAddresses() {
		return emailAddresses;
	}
	public void setEmailAddresses(List<EMailAddress> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	
}
