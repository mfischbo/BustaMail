package de.mfischbo.bustamail.subscriber.domain;

import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.common.domain.Gender;

@Entity
@Table(name = "Subscriber_Contact")
public class Contact extends BaseDomain {

	@Basic(fetch = FetchType.EAGER)
	private String title;
	
	@Basic(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	@Basic(fetch = FetchType.EAGER)
	private String firstName;
	
	@Basic (fetch = FetchType.EAGER)
	private String lastName;

	@Basic(fetch = FetchType.EAGER)
	private boolean formalSalutation;

	@OneToMany(mappedBy = "contact")
	private Set<Address>		addresses;

	@OneToMany(fetch = FetchType.LAZY)
	private List<EMailAddress>	emailAddresses;

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

	public Set<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}

	public List<EMailAddress> getEmailAddresses() {
		return emailAddresses;
	}

	public void setEmailAddresses(List<EMailAddress> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}
}
