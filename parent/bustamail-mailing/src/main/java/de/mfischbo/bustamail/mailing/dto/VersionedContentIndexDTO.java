package de.mfischbo.bustamail.mailing.dto;

import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.dto.BaseDTO;
import de.mfischbo.bustamail.mailing.domain.VersionedContent.ContentType;
import de.mfischbo.bustamail.security.dto.UserDTO;

public class VersionedContentIndexDTO extends BaseDTO {

	private static final long serialVersionUID = -6034298109001208567L;
	private DateTime			dateCreated;
	private UserDTO				userCreated;
	private ContentType			type;
	
	
	public DateTime getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}
	public UserDTO getUserCreated() {
		return userCreated;
	}
	public void setUserCreated(UserDTO userCreated) {
		this.userCreated = userCreated;
	}
	public ContentType getType() {
		return type;
	}
	public void setType(ContentType type) {
		this.type = type;
	}
}
