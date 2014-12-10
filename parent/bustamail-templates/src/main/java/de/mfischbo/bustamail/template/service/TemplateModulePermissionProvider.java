package de.mfischbo.bustamail.template.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import de.mfischbo.bustamail.security.domain.Permission;
import de.mfischbo.bustamail.security.service.PermissionProvider;

@Component
public class TemplateModulePermissionProvider implements PermissionProvider {

	@Override
	public Set<Permission> getModulePermissions() {
		Set<Permission> retval = new HashSet<Permission>();
		
		retval.add(new Permission(UUID.fromString("f63d6ed0-2dec-4ea4-a008-c2e05aa80711"),
				"Templates", "Templates.USE_TEMPLATES", "Templates verwenden", "Kann Templates verwenden, diese aber nicht editieren"));
		retval.add(new Permission(UUID.fromString("f754b99b-a79d-4f83-a9ce-bdbdde2e95b9"),
				"Templates", "Templates.MANAGE_TEMPLATES", "Templates verwalten", "Kann neue Templates anlegen, bearbeiten und entfernen"));
		return retval;
	}
}