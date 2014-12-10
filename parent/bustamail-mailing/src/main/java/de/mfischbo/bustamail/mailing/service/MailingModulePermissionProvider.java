package de.mfischbo.bustamail.mailing.service;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import de.mfischbo.bustamail.security.domain.Permission;
import de.mfischbo.bustamail.security.service.PermissionProvider;

@Component
public class MailingModulePermissionProvider implements PermissionProvider {

	@Override
	public Set<Permission> getModulePermissions() {
		
		Set<Permission> perms = new LinkedHashSet<Permission>();
		perms.add(new Permission(UUID.fromString("e572f255-3f7f-4c09-a871-88d475725bc4"),
				"Mailings", "Mailings.USE_MAILINGS", "Mailings verwenden", "Benutzer kann Mailings sehen"));
		
		perms.add(new Permission(UUID.fromString("bcb8f7c9-e44a-44ea-a02b-71ee3eec7790"),
				"Mailings", "Mailings.MANAGE_MAILINGS", "Mailings verwalten", "Benutzer kann Mailings anlegen bearbeiten und entfernen"));
		
		perms.add(new Permission(UUID.fromString("aa4b321d-e53e-415c-912a-21f07dcb4b17"),
				"Mailings", "Mailings.EDIT_CONTENTS", "Inhalte editieren", "Der Benutzer darf den Inhalt eines Mailings editieren"));
		
		perms.add(new Permission(UUID.fromString("b9303266-f7a3-4f5d-b373-bb77463ae7d4"),
				"Mailings", "Mailings.APPROVE_MAILINGS", "Freigabe erteilen", "Der Benutzer darf ein Mailing zum versand freigeben"));
		
		perms.add(new Permission(UUID.fromString("64e11693-553b-4f02-998a-e79cb2bc4f97"),
				"Mailings", "Mailings.PUBLISH_MAILINGS", "Mailing versenden", "Der Benutzer darf ein Mailing versenden"));

		return perms;
	}

}
