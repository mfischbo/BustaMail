package de.mfischbo.bustamail.bouncemail.service;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import de.mfischbo.bustamail.security.domain.Permission;
import de.mfischbo.bustamail.security.service.PermissionProvider;

@Component
public class ModulePermissionProvider implements PermissionProvider {

	@Override
	public Set<Permission> getModulePermissions() {
		
		Set<Permission> perms = new LinkedHashSet<>();
		
		perms.add(new Permission(UUID.fromString("db077963-f974-4b08-acea-ea7d0a1752cd"), 
				"Bounce Accounts", "BounceAccounts.USE_ACCOUNTS", "Bounce Accounts verwenden", "Benutzer kann Bounce Accounts sehen"));
		
		perms.add(new Permission(UUID.fromString("89f766f1-23ac-4afd-9f64-135ac5267d27"),
				"Bounce Accounts", "BounceAccounts.MANAGE_ACCOUNTS", "Bounce Accounts verwalten", "Benutzer kann Accounts verwalten"));
		
		perms.add(new Permission(UUID.fromString("01b2ffcb-92b5-4479-842c-d99122ac0e0d"),
				"Bounce Mails", "BounceAccounts.READ_MAILS", "Bounce Mails lesen", "Benutzer kann die Mails eines Accounts lesen"));
		
		perms.add(new Permission(UUID.fromString("01b2ffcb-92b5-4479-842c-d99122ac0e0f"),
				"Bounce Mails", "BounceAccounts.MANAGE_MAILS", "Bounce Mails bearbeiten", "Benutzer kann die Mails eines Accounts bearbeiten"));
		return perms;
	}
}
