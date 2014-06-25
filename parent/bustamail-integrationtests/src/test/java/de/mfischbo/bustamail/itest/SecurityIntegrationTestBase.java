package de.mfischbo.bustamail.itest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import de.mfischbo.bustamail.common.ApplicationConfig;
import de.mfischbo.bustamail.security.dto.OrgUnitDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("testing")
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
