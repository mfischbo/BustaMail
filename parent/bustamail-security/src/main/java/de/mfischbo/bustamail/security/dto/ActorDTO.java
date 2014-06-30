package de.mfischbo.bustamail.security.dto;

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ActorDTO {

	private UUID				id;
	
	@JsonIgnore
	private UserDTO				user;
	private UUID				userId;

	@JsonIgnore
	private OrgUnitDTO			orgUnit;
	private UUID				orgUnitId;
	
	private boolean				addToChildren;
	private boolean				addToFutureChildren;
	
	private Set<UUID>			permissions;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
		this.userId = user.getId();
	}

	public UUID getUserId() {
		if (user != null)
			return user.getId();
		return userId;
	}

	public void setUserId(UUID userId) {
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

	public UUID getOrgUnitId() {
		if (orgUnit != null)
			return orgUnit.getId();
		return orgUnitId;
	}

	public void setOrgUnitId(UUID orgUnitId) {
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
