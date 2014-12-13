package de.mfischbo.bustamail.security.domain;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.DBRef;

import de.mfischbo.bustamail.common.domain.BaseDomain;

public class Actor extends BaseDomain {

	private static final long serialVersionUID = -450042238744926288L;

	@DBRef
	private User 		user;
	
	private boolean		addToChildren;
	
	private boolean		addToFutureChildren;
	
	private Set<UUID>	permissions;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Actor [user=" + user + ", "
				+ ", addToChildren=" + addToChildren + ", addToFutureChildren="
				+ addToFutureChildren + ", permissions=" + permissions.size() + "]";
	}
	
}
