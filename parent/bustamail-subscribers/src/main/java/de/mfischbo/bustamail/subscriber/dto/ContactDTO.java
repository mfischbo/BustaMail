package de.mfischbo.bustamail.subscriber.dto;

import java.util.List;
import java.util.UUID;

public class ContactDTO {

	private UUID						id;
	private String 						firstName;
	private String						lastName;
	private boolean						formalSalutation;
	private List<ContactAttributeDTO>	attributes;
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
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
	public List<ContactAttributeDTO> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<ContactAttributeDTO> attributes) {
		this.attributes = attributes;
	}
}
