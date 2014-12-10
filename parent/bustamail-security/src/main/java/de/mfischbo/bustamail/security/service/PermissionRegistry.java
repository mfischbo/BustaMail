package de.mfischbo.bustamail.security.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.mfischbo.bustamail.security.domain.Permission;

@Component
public class PermissionRegistry {

	private Set<Permission> sysPerms = new HashSet<Permission>();

	@Inject
	private List<PermissionProvider> providers;
	
	private Logger log = LoggerFactory.getLogger(getClass());

	
	@PostConstruct
	public void initializePermissions() {
		for (PermissionProvider p : providers) {
			log.info("Adding persmissions from provider : " + p.getClass().getCanonicalName());
			sysPerms.addAll(p.getModulePermissions());
		}
	}

	public Set<Permission> getSystemPermissions() {
		return Collections.unmodifiableSet(this.sysPerms);
	}
	
	public Permission getPermissionByIdentificator(String identificator) {
		for (Permission p : sysPerms) 
			if (p.getIdentificator().equals(identificator))
				return p;
		return null;
	}
}