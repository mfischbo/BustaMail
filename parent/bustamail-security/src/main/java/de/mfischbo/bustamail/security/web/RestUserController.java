package de.mfischbo.bustamail.security.web;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.security.dto.ActorDTO;
import de.mfischbo.bustamail.security.dto.AuthenticationDTO;
import de.mfischbo.bustamail.security.dto.OrgUnitDTO;
import de.mfischbo.bustamail.security.dto.UserDTO;
import de.mfischbo.bustamail.security.dto.UserPasswordDTO;
import de.mfischbo.bustamail.security.service.SecurityService;

@RestController
@RequestMapping("/api/security/users")
public class RestUserController extends BaseApiController {

	@Autowired
	private SecurityService service;
	

	@RequestMapping(value = "", method = RequestMethod.GET)
	public Page<UserDTO> findUsers(@RequestParam(value = "query", required=false) String searchTerm,
			@PageableDefault(page=0, size = 30) Pageable page) {
		if (searchTerm == null || searchTerm.length() == 0)
			return service.getAllUsers(page);
		else
			return service.findUsers(searchTerm, page);
	}
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public UserDTO getUserById(@PathVariable("id") UUID userId) throws Exception {
		return service.getUserById(userId);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public UserDTO updateUser(@PathVariable("id") UUID userId, @RequestBody UserDTO user) throws Exception {
		user.setId(userId);
		return service.updateUser(user);
	}
	
	@RequestMapping(value = "/orgUnits", method = RequestMethod.GET)
	public Set<OrgUnitDTO> getCurrentUsersOrgUnits() {
		return asDTO(service.getOrgUnitsByCurrentUser(), OrgUnitDTO.class);
	}
	
	@RequestMapping(value = "/{id}/actors", method = RequestMethod.POST)
	public Set<ActorDTO> updateActors(@PathVariable("id") UUID userId, @RequestBody List<ActorDTO> actors) throws Exception {
		return service.updateActors(userId, actors);
	}

	@RequestMapping(value = "/{id}/password", method = RequestMethod.POST)
	public void setPassword(@PathVariable("id") UUID userId, @RequestBody UserPasswordDTO dto) throws Exception {
		service.setUserPassword(userId, dto);
	}
	
	@RequestMapping(value = "/{id}/lock", method = RequestMethod.PUT)
	public UserDTO lockUser(@PathVariable("id") UUID userId) throws Exception {
		return service.lockUser(userId);
	}
	
	@RequestMapping(value = "/{id}/lock", method = RequestMethod.DELETE)
	public UserDTO unlockUser(@PathVariable("id") UUID userId) throws Exception {
		return service.unlockUser(userId);
	}
	
	@RequestMapping(value = "/recover", method = RequestMethod.POST)
	public void recoverUserPassword(@RequestBody AuthenticationDTO dto) throws Exception {
		service.recoverUserPassword(dto);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteUser(@PathVariable("id") UUID userId) throws Exception {
		service.deleteUser(userId);
	}
}
