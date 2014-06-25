package de.mfischbo.bustamail.subscriber.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.mfischbo.bustamail.common.domain.BaseDomain;

@Entity
@Table(name = "Subscriber_ContactAttribute")
public class ContactAttribute extends BaseDomain {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "Contact_id", referencedColumnName = "id", nullable = false)
	private Contact					contact;
	
	@Basic(fetch = FetchType.EAGER, optional = false)
	@Column(name = "attributeKey")
	@Enumerated(EnumType.STRING)
	private ContactAttributeKey		key;
	
	@Basic(fetch = FetchType.EAGER, optional = false)
	@Column(name = "attributeValue")
	private String					value;
	
	public ContactAttribute() {
		
	}

	public ContactAttributeKey getKey() {
		return key;
	}

	public void setKey(ContactAttributeKey key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
