package de.mfischbo.bustamail.subscriber.domain;

import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
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

	private static final long serialVersionUID = -7625104438651224536L;

	@Basic(fetch = FetchType.EAGER)
	private String title;
	
	@Basic(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	private Gender gender = Gender.N;
	
	@Basic(fetch = FetchType.EAGER)
	private String firstName;
	
	@Basic (fetch = FetchType.EAGER)
	private String lastName;

	@Basic(fetch = FetchType.EAGER)
	private boolean formalSalutation;

	@OneToMany(mappedBy = "contact", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Address>		addresses;

	@OneToMany(mappedBy = "contact", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
