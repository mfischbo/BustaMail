package de.mfischbo.bustamail.security.domain;

import java.util.Set;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mfischbo.bustamail.common.domain.BaseDomain;

@Document(collection = "OrgUnit")
public class OrgUnit extends BaseDomain {

	private static final long serialVersionUID = 1410454016750659990L;

	@NotBlank
	@Indexed
	private String			name;
	
	private	String			description;

	private DateTime		dateCreated;
	
	private DateTime 		dateModified;
	
	private boolean			deleted;
	
	@DBRef
	private OrgUnit		parent;
	
	private Set<Actor> 	actors;
	

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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public OrgUnit getParent() {
		return parent;
	}

	public void setParent(OrgUnit parent) {
		this.parent = parent;
	}

	public Set<Actor> getActors() {
		return actors;
	}

	public void setActors(Set<Actor> actors) {
		this.actors = actors;
	}

	@Override
	public String toString() {
		return "OrgUnit [name=" + name + ", id=" + id + "]";
	}
}
