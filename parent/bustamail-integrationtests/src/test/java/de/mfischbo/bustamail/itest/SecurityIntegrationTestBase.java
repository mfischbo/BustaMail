package de.mfischbo.bustamail.itest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

import de.mfischbo.bustamail.security.dto.OrgUnitDTO;

public class SecurityIntegrationTestBase extends AbstractIntegrationTestBase {

	
	public void canCreateTopLevelOrgUnitHavingPermission() throws Exception {
		OrgUnitDTO ou = new OrgUnitDTO();
		ou.setName("Test Top Level Unit");
		ou.setDescription("Some descriptional text");
		
		mock.perform(post(BASE + "/api/security/orgUnits")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(ou))
				.session(getPermittedSession()))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.id").exists());
	}
	
	
	public void forbidsCreateTopLevelOrgUnitNotHavingPermission() throws Exception {
		OrgUnitDTO ou = new OrgUnitDTO();
		ou.setName("Test Top Level Unit");
		ou.setDescription("Some descriptional text");
		
		mock.perform(post(BASE + "/api/security/orgUnits")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(ou))
				.session(getUnpermittedSession()))
				.andExpect(status().isForbidden());
	}
}
