package de.mfischbo.bustamail.vc.domain;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.security.domain.User;

@Document(collection="VersionedContent_VersionedContent")
public class VersionedContent extends BaseDomain {

	private static final long serialVersionUID = 2960183880457152474L;

	public enum ContentType {
		HTML,
		Text
	}
	
	private DateTime		dateCreated;
	
	@DBRef
	private User			userCreated;

	private ObjectId		foreignId;
	
	private String			content;

	private ContentType		type;
	
	public DateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public User getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(User userCreated) {
		this.userCreated = userCreated;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ObjectId getForeignId() {
		return foreignId;
	}

	public void setForeignId(ObjectId foreignId) {
		this.foreignId = foreignId;
	}

	public ContentType getType() {
		return type;
	}

	public void setType(ContentType type) {
		this.type = type;
	}
}
