package de.mfischbo.bustamail.itest;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Test;
import org.springframework.http.MediaType;

import de.mfischbo.bustamail.mailinglist.dto.SubscriptionListDTO;


public class MailingListIT extends AbstractIntegrationTestBase {

	private static final UUID TEST_LIST_ID  = UUID.fromString(toUUIDString("DA66D81495BE4AD6A1F66A0EEC414ED9"));

	private static final String API_PATH = "/api/subscription-lists";
	
	
	@Test
	public void canReadOrgUnitsMailingListsWhenHavingPermission() throws Exception {
		mock.perform(get(BASE + API_PATH + "?owner=" + TEST_OWNER_ID)
				.session(getPermittedSession())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content", hasSize(1)));
	}

	@Test
	public void returnsNoResultsForForeignOrgUnit() throws Exception {
		mock.perform(get(BASE + API_PATH + "?owner=" + TEST_OWNER_ID)
				.session(getForeignSession())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}


	@Test
	public void canGetByIdWhenMemberOfOrgUnit() throws Exception {
		mock.perform(get(BASE + API_PATH + "/{id}", TEST_LIST_ID.toString()) 
				.session(getPermittedSession()) 
				.accept(MediaType.APPLICATION_JSON)) 
				.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(TEST_LIST_ID.toString()));
	}

	@Test
	public void returnsNotFoundForGetByIdWhenForeignOrgUnit() throws Exception {
		mock.perform(get(BASE + API_PATH + "/{id}", TEST_LIST_ID.toString()) 
				.session(getForeignSession()) 
				.accept(MediaType.APPLICATION_JSON)) 
			.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void canCreateSubscriptionListWhenHavingPermission() throws Exception {
		SubscriptionListDTO list = new SubscriptionListDTO();
		list.setName("Test List");
		list.setDescription("Test Description");
		list.setOwner(TEST_OWNER_ID);
		
		mock.perform(post(BASE + API_PATH)
			.session(getPermittedSession())
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsString(list)))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().contentType(mtJson))
			.andExpect(jsonPath("$.id").exists());
	}
	
	@Test
	public void forbidsCreateSubscriptionListWhenNotHavingPermission() throws Exception {
		SubscriptionListDTO list = new SubscriptionListDTO();
		list.setName("Test List");
		list.setDescription("Test description");
		list.setOwner(TEST_OWNER_ID);
		
		mock.perform(post(BASE + API_PATH)
				.session(getUnpermittedSession())
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(list))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}


	@Test
	public void forbidsCreateForForeignOrgUnit() throws Exception {
		SubscriptionListDTO list = new SubscriptionListDTO();
		list.setName("Test list not to be created");
		list.setDescription("Test list not to be created");
		list.setOwner(TEST_OWNER_ID);
	
		mock.perform(post(BASE + API_PATH)
				.session(getForeignSession())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}

	
	@Test
	public void canUpdateSubscriptionListWhenHavingPermission() throws Exception {
		SubscriptionListDTO list = new SubscriptionListDTO();
		list.setId(TEST_LIST_ID);
		list.setName("Test for update list");
		list.setDescription("Test for update list");
		list.setOwner(TEST_OWNER_ID);
		
		mock.perform(patch(BASE + API_PATH + "/{id}", TEST_LIST_ID.toString())
				.session(getPermittedSession())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(list)))
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().contentType(mtJson))
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.id").value(TEST_LIST_ID.toString()))
				.andExpect(jsonPath("$.name").value("Test for update list"))
				.andExpect(jsonPath("$.description").value("Test for update list"));
	}
	
	@Test
	public void fobidsUpdateSubscriptionListWhenNotHavingPermission() throws Exception {
		SubscriptionListDTO list = new SubscriptionListDTO();
		list.setId(TEST_LIST_ID);
		list.setName("New Name that will not be set");
		list.setDescription("New description");
		
		mock.perform(patch(BASE + API_PATH + "/{id}", TEST_LIST_ID.toString())
				.session(getUnpermittedSession())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(list)))
			.andExpect(status().isForbidden());
	}

	@Test
	public void returnsNotFoundForUpdateSubscriptionListForForeignOrgUnit() throws Exception {
		SubscriptionListDTO list = new SubscriptionListDTO();
		list.setId(TEST_LIST_ID);
		list.setName("New Name that will not be set");
		list.setDescription("New description");
		
		mock.perform(patch(BASE + API_PATH + "/{id}", TEST_LIST_ID.toString())
				.session(getForeignSession())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(list)))
			.andExpect(status().isUnauthorized());
	}
	

	@Test
	public void canDeleteSubscriptionListWhenHavingPermission() throws Exception {
		mock.perform(delete(BASE + API_PATH + "/{id}", TEST_LIST_ID.toString())
				.session(getPermittedSession()))
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void forbidsDeleteSubscriptionListNotHavingPermission() throws Exception {
		mock.perform(delete(BASE + API_PATH + "/{id}", TEST_LIST_ID.toString())
				.session(getUnpermittedSession()))
				.andExpect(status().isForbidden());
	}

	@Test
	public void unauthorizedForDeleteSubscriptionListForForeignOrgUnit() throws Exception {
			mock.perform(delete(BASE + API_PATH + "/{id}", TEST_LIST_ID.toString())
				.session(getForeignSession()))
				.andExpect(status().isUnauthorized());
	}
}
