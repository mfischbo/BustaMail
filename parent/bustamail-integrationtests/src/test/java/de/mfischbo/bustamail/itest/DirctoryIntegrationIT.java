package de.mfischbo.bustamail.itest;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;

import de.mfischbo.bustamail.media.dto.DirectoryDTO;

public class DirctoryIntegrationIT extends AbstractIntegrationTestBase {

	private static final String TEST_ROOT_DIR_ID = "3F9D5D9D534F789D209D1C194A3C9D00";
	
	@Test
	public void canReadOrgUnitsRootDirectoryWhenHavingPermission() throws Exception {
		mock.perform(get(BASE + "/api/media/directory")
				.session(getPermittedSession())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.parseMediaType("application/json;charset=UTF-8")))
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].id").value(toUUIDString(TEST_ROOT_DIR_ID)));
	}
	
	
	@Test
	public void canCreateSubFolderInRootWhenHavingPermission() throws Exception {
		DirectoryDTO d = new DirectoryDTO();
		d.setName("Subfolder from tests");
		d.setDescription("Some description");
		
		mock.perform(put(BASE + "/api/media/directory/{id}", toUUIDString(TEST_ROOT_DIR_ID))
			.session(getPermittedSession())
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsString(d)))
			.andDo(print())
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().contentType(MediaType.parseMediaType("application/json;charset=UTF-8")))
			.andExpect(jsonPath("$.name").value("Subfolder from tests"));
	}
	
	@Test
	public void forbidsCreateSubFolderInRootNotHavingPermission() throws Exception {
		DirectoryDTO d = new DirectoryDTO();
		d.setName("Not to be created");
		d.setDescription("Some description");
		
		mock.perform(put(BASE + "/api/media/directory/{id}", toUUIDString(TEST_ROOT_DIR_ID))
				.session(getUnpermittedSession())
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(d)))
				.andExpect(status().isForbidden());
	}
	
	@Test
	public void statesConflictDeletingRootFolderHavingPermission() throws Exception {
		mock.perform(delete(BASE + "/api/media/directory/{id}", toUUIDString(TEST_ROOT_DIR_ID))
				.session(getPermittedSession()))
				.andExpect(status().isConflict());
	}
	
	@Test
	public void forbidsDeletingRootFolderNotHavingPermission() throws Exception {
		mock.perform(delete(BASE + "/api/media/directory/{id}", toUUIDString(TEST_ROOT_DIR_ID))
				.session(getUnpermittedSession()))
				.andExpect(status().isForbidden());
	}
}
