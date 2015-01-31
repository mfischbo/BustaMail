package de.mfischbo.bustamail.mailinglist.domain;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;

@Document(collection = "MailingList_SubscriptionList")
public class SubscriptionList extends OwnedBaseDomain {

	private static final long serialVersionUID = -5335592314616788305L;

	@NotBlank
	@Indexed
	private String			name;
	
	private String			description;
	
	private boolean			publiclyAvailable;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPubliclyAvailable() {
		return publiclyAvailable;
	}

	public void setPubliclyAvailable(boolean publiclyAvailable) {
		this.publiclyAvailable = publiclyAvailable;
	}
}