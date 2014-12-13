package de.mfischbo.bustamail.security.dto;

import java.util.List;

import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.domain.Gender;
import de.mfischbo.bustamail.common.dto.BaseDTO;

public class UserDTO extends BaseDTO {

	private static final long serialVersionUID = -5214230636220530173L;
	
	private String		firstName;
	private String		lastName;
	private Gender		gender;
	private String		email;
	private DateTime	dateCreated;
	private DateTime	dateModified;
	private boolean		locked;
	private boolean		deleted;
	
	
	private List<ActorDTO>		actors;

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
	
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<ActorDTO> getActors() {
		return actors;
	}

	public void setActors(List<ActorDTO> actors) {
		this.actors = actors;
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

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
