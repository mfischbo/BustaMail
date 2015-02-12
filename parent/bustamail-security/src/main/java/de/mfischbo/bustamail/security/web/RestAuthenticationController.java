package de.mfischbo.bustamail.security.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.security.dto.AuthenticationDTO;
import de.mfischbo.bustamail.security.dto.UserDTO;
import de.mfischbo.bustamail.security.service.SecurityService;

@RestController
@RequestMapping("/api/security/authentication")
public class RestAuthenticationController extends BaseApiController {

	@Autowired
	private SecurityService		service;
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public UserDTO signIn(@RequestBody AuthenticationDTO dto) {
		return service.signIn(dto);
	}
	
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public void signOut(HttpServletRequest req, HttpServletResponse res) {
		service.signOut(req, res);
	}
}
