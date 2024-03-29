package de.mfischbo.bustamail.mailinglist.dto;

import de.mfischbo.bustamail.common.dto.OwnedBaseDTO;

public class SubscriptionListDTO extends OwnedBaseDTO {

	private static final long serialVersionUID = -7912261685818363463L;

	private String			name;
	private String			description;
	private boolean			publiclyAvailable;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isPubliclyAvailable() {
		return publiclyAvailable;
	}
	public void setPubliclyAvailable(boolean publiclyAvailable) {
		this.publiclyAvailable = publiclyAvailable;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
