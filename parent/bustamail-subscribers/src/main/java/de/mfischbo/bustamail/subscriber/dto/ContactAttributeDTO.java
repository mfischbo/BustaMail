package de.mfischbo.bustamail.subscriber.dto;

import java.util.UUID;

import de.mfischbo.bustamail.subscriber.domain.ContactAttributeKey;

public class ContactAttributeDTO {

	private UUID id;
	private ContactAttributeKey		key;
	private String					value;
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
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
