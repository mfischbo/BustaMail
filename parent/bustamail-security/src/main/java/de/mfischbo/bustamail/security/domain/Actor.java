package de.mfischbo.bustamail.security.domain;

import java.util.Set;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.mfischbo.bustamail.common.domain.BaseDomain;

@Entity
@Table(name = "Security_Actor")//, uniqueConstraints = {
//		@UniqueConstraint(columnNames = {"OrgUnit_id", "User_id"})
//})
public class Actor extends BaseDomain {

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "User_id", referencedColumnName = "id")
	private User 		user;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "OrgUnit_id", referencedColumnName = "id")
	private OrgUnit 	orgUnit;
	
	@Basic(fetch = FetchType.EAGER)
	private boolean		addToChildren;
	
	@Basic(fetch = FetchType.EAGER)
	private boolean		addToFutureChildren;
	
	@CollectionTable(name = "Security_Actor_Permission")
	@ElementCollection
	@Column(name = "Permission_id", length = 16)
	private Set<UUID>	permissions;
	
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public OrgUnit getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(OrgUnit orgUnit) {
		this.orgUnit = orgUnit;
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

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (addToChildren ? 1231 : 1237);
		result = prime * result + (addToFutureChildren ? 1231 : 1237);
		result = prime * result + ((orgUnit == null) ? 0 : orgUnit.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		Actor other = (Actor) obj;
		if (addToChildren != other.addToChildren)
			return false;
		if (addToFutureChildren != other.addToFutureChildren)
			return false;
		if (orgUnit == null) {
			if (other.orgUnit != null)
				return false;
		} else if (!orgUnit.equals(other.orgUnit))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Actor [user=" + user + ", orgUnit=" + orgUnit
				+ ", addToChildren=" + addToChildren + ", addToFutureChildren="
				+ addToFutureChildren + ", permissions=" + permissions.size() + "]";
	}
	
}
