package de.mfischbo.bustamail.security.dto;

import java.util.Set;
import java.util.UUID;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.mfischbo.bustamail.common.dto.BaseDTO;

public class ActorDTO extends BaseDTO {

	private static final long serialVersionUID = 6427625273688569714L;
	
	@JsonIgnore
	private UserDTO				user;
	private ObjectId			userId;

	@JsonIgnore
	private OrgUnitDTO			orgUnit;
	private ObjectId			orgUnitId;
	
	private boolean				addToChildren;
	private boolean				addToFutureChildren;
	
	private Set<UUID>			permissions;

	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
		this.userId = user.getId();
	}

	public ObjectId getUserId() {
		if (user != null)
			return user.getId();
		return userId;
	}

	public void setUserId(ObjectId userId) {
		if (userId != null && this.userId == null)
			this.userId = userId;
	}

	public OrgUnitDTO getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(OrgUnitDTO orgUnit) {
		this.orgUnit = orgUnit;
		this.orgUnitId = orgUnit.getId();
	}

	public ObjectId getOrgUnitId() {
		if (orgUnit != null)
			return orgUnit.getId();
		return orgUnitId;
	}

	public void setOrgUnitId(ObjectId orgUnitId) {
		if (orgUnitId != null && this.orgUnitId == null)
			this.orgUnitId = orgUnitId;
	}

	public boolean isAddToChildren() {
		return addToChildren;
	}

	public void setAddToChildren(boolean addToChildren) {
		this.addToChildren = addToChildren;
	}

	public boolean isAddToFutureChildren() {
		return addToFutureChildren;
	}

	public void setAddToFutureChildren(boolean addToFutureChildren) {
		this.addToFutureChildren = addToFutureChildren;
	}

	public Set<UUID> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<UUID> permissions) {
		this.permissions = permissions;
	}
}
