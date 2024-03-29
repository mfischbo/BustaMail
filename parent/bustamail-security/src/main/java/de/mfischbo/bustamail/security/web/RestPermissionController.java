package de.mfischbo.bustamail.security.web;

import java.util.Set;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.security.domain.Permission;
import de.mfischbo.bustamail.security.service.PermissionRegistry;

@RestController
@RequestMapping("/api/security/permissions")
public class RestPermissionController extends BaseApiController {

	@Inject
	private PermissionRegistry registry;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Set<Permission> getSystemPermissions() {
		return registry.getSystemPermissions();
	}
}
