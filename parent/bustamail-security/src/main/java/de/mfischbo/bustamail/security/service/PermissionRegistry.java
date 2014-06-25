package de.mfischbo.bustamail.security.service;

import java.util.HashSet;
import java.util.Set;

import de.mfischbo.bustamail.security.domain.Permission;

public class PermissionRegistry {

	static private Set<Permission> sysPerms = new HashSet<Permission>();
	
	public static void registerPermissions(Set<Permission> permissions) {
		sysPerms.addAll(permissions);
	}
	
	public static Set<Permission> getSystemPermissions() {
		return sysPerms;
	}
	
	public static Permission getPermissionByIdentificator(String identificator) {
		for (Permission p : sysPerms) 
			if (p.getIdentificator().equals(identificator))
				return p;
		return null;
	}

}
