package de.mfischbo.bustamail.security.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import de.mfischbo.bustamail.security.domain.Permission;

@Component
public class SecurityModulePermissionProvider implements PermissionProvider {

	@Override
	public Set<Permission> getModulePermissions() {
	
		Set<Permission> retval = new HashSet<Permission>();
		retval.add(new Permission(UUID.fromString("3ea312ee-637b-4ee9-84a5-446ed7cfa061"), 
				"Security", "Security.MANAGE_USER", "Benutzer verwalten", "Darf Benutzer verwalten"));
		
		retval.add(new Permission(UUID.fromString("2f2fe0c6-3b96-4893-b80b-da85adffe8fe"),
				"Security", "Security.MANAGE_ORG_UNITS", "Organisationseinheiten verwalten", "Darf Organisationseinheiten verwalten"));
		
		return retval;
	}
}
