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
	public Set<Address>		addresses;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "contact")
	private List<ContactAttribute> 		attributes;
	
	
}
