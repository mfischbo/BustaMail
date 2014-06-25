package de.mfischbo.bustamail.media.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import de.mfischbo.bustamail.security.domain.Permission;
import de.mfischbo.bustamail.security.service.PermissionProvider;

public class MediaModulePermissionProvider implements PermissionProvider {

	@Override
	public Set<Permission> getModulePermissions() {
	
		Set<Permission> perms = new HashSet<Permission>();
		perms.add(new Permission(UUID.fromString("3739bfd0-e4f1-4b50-abb8-f7fdb6e13fbf"), 
				"Medien", "Media.USE_MEDIA", "Medieninhalte verwenden", "Darf Medieninhalte verwenden"));
		perms.add(new Permission(UUID.fromString("f9f95c32-a031-46f0-a9e7-e3416bb8f922"), 
				"Medien", "Media.MANAGE_MEDIA", "Medieninhalte verwalten", "Darf Medieninhalte hochladen, bearbeiten und entfernen"));
		return perms;
	}

	
}
