package de.mfischbo.bustamail.security.service;

import java.util.Set;

import de.mfischbo.bustamail.security.domain.Permission;

public interface PermissionProvider {

	/**
	 * Returns a set of permissions the module provides
	 * @return The set of permissions. Might be empty or null
	 */
	public Set<Permission> getModulePermissions();
}
