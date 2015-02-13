package de.mfischbo.bustamail.media.domain;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;

@Document(collection = "Media_Directory")
public class Directory extends OwnedBaseDomain {

	private static final long serialVersionUID = -1066888517355528723L;

	@Indexed
	private String			name;
	
	private String			description;
	
	private DateTime		dateCreated;
	
	private DateTime		dateModified;
	
	private ObjectId		parent;

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
	public ObjectId getParent() {
		return parent;
	}
	public void setParent(ObjectId parent) {
		this.parent = parent;
	}
	public DateTime getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}
	public DateTime getDateModified() {
		return dateModified;
	}
	public void setDateModified(DateTime dateModified) {
		this.dateModified = dateModified;
	}
}
