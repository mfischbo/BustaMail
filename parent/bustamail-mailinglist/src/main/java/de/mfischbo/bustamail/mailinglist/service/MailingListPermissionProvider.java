package de.mfischbo.bustamail.mailinglist.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import de.mfischbo.bustamail.security.domain.Permission;
import de.mfischbo.bustamail.security.service.PermissionProvider;

public class MailingListPermissionProvider implements PermissionProvider {

	@Override
	public Set<Permission> getModulePermissions() {
		Set<Permission> retval = new HashSet<>();
		retval.add(new Permission(UUID.fromString("9f1fa3b4-d7c0-49fe-8472-d52fb65ae25d"), 
				"Verteilerlisten", "MailingList.MANAGE_SUBSCRIPTION_LISTS", "Verteilerlisten verwalten", 
				"Benutzer darf Verteilerlisten anlegen, bearbeiten und entfernen"));
		return retval;
	}

}
