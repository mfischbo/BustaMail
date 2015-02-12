package de.mfischbo.bustamail.optin.service;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import de.mfischbo.bustamail.security.domain.Permission;
import de.mfischbo.bustamail.security.service.PermissionProvider;

@Component
public class OptinModulePermissionProvider implements PermissionProvider {

	@Override
	public Set<Permission> getModulePermissions() {

		Set<Permission> retval = new LinkedHashSet<Permission>();
		
		retval.add(new Permission(UUID.fromString("6c51e1d6-61a8-4da8-ad82-74ac3365d23c"),
				"Optin Mails", "OptinMails.USE_OPTIN_MAILS", "Optin Mails verwenden", "Benutzer darf Optin Mails verwenden"));
		
		retval.add(new Permission(UUID.fromString("fcc0b5e8-a8df-48af-b3c5-96f07a49a892"), 
				"Optin Mails", "OptinMails.MANAGE_OPTIN_MAILS", "Optin Mails verwalten", "Benutzer darf Optin Mails verwalten"));
		
		return retval;
	}
}
