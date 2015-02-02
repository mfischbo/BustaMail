package de.mfischbo.bustamail.security.dto;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.dto.BaseDTO;

public class OrgUnitDTO extends BaseDTO {

	
	private static final long serialVersionUID = -6308592467692762523L;
	
	private		String				name;
	private		String				description;
	private		DateTime			dateCreated;
	private		DateTime			dateModified;
	private		boolean				deleted;
	private		Set<OrgUnitDTO>		children = new LinkedHashSet<>();
	private		List<ActorDTO>		actors;

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
	public Set<OrgUnitDTO> getChildren() {
		return children;
	}
	public void setChildren(Set<OrgUnitDTO> children) {
		this.children = children;
	}
	public List<ActorDTO> getActors() {
		return actors;
	}
	public void setActors(List<ActorDTO> actors) {
		this.actors = actors;
	}
}
