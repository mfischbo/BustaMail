package de.mfischbo.bustamail.security.web;

import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.dto.OrgUnitDTO;
import de.mfischbo.bustamail.security.dto.UserDTO;
import de.mfischbo.bustamail.security.service.SecurityService;

@RestController
@RequestMapping(value = "/api/security/orgUnits")
public class RestOrgUnitController extends BaseApiController {

	@Autowired
	private	SecurityService			service;
	
	@RequestMapping(value = "/roots", method = RequestMethod.GET)
	public Set<OrgUnitDTO> getRootOrgUnits() {
		return service.getTopLevelUnits();
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public OrgUnitDTO createOrgUnit(@RequestBody OrgUnitDTO dto) throws Exception {
		
		OrgUnitDTO root = service.getRootOrgUnit();
		return service.createOrgUnit(root.getId(), dto);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public OrgUnitDTO getOrgUnitById(@PathVariable("id") ObjectId id) throws Exception {
		return asDTO(service.getOrgUnitById(id), OrgUnitDTO.class);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public OrgUnitDTO createOrgUnitForParent(@PathVariable("id") ObjectId parentId, @RequestBody OrgUnitDTO dto) throws Exception {
		return service.createOrgUnit(parentId, dto);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public OrgUnitDTO updateOrgUnit(@PathVariable("id") ObjectId id, @RequestBody OrgUnitDTO dto) throws Exception {
		return service.updateOrgUnit(dto);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteOrgUnit(@PathVariable("id") ObjectId id) throws Exception {
		service.deleteOrgUnit(id);
	}
	
	@RequestMapping(value = "/{id}/users", method = RequestMethod.POST)
	public UserDTO createUser(@PathVariable("id") ObjectId owner, @RequestBody UserDTO user) throws Exception {
		return service.createUser(owner, user);
	}
	
	@RequestMapping(value = "/{id}/users", method = RequestMethod.GET)
	public Page<UserDTO> getUsersByOrgUnit(@PathVariable("id") ObjectId id, @PageableDefault(page = 0, size = 30, sort = "lastName") Pageable page) throws Exception {
		OrgUnit o = service.getOrgUnitById(id);
		return service.getUsersByOrgUnit(o, page);
	}
}
