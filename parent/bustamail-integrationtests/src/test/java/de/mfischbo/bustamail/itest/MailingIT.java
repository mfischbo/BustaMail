package de.mfischbo.bustamail.itest;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Test;
import org.springframework.http.MediaType;

import de.mfischbo.bustamail.mailing.dto.MailingDTO;
import de.mfischbo.bustamail.template.dto.TemplateIndexDTO;

public class MailingIT extends AbstractIntegrationTestBase {

	public static final String M_PATH = "/api/mailings";
	public static final String URL    = BASE + M_PATH + "/";
		
	public static final String NEW_MAILING_ID= toUUIDString("195F5FAF44EE4ACBAE1E736875137F16");
	public static final String AR_MAILING_ID = toUUIDString("D75AD50DB7904B0EB64A9B1F47E82695");
	public static final String AP_MAILING_ID = toUUIDString("D75AD50DB7904B0EB64A9B1F47E82697");
	public static final String TEST_TEMPLATE = toUUIDString("AF66E41495BE4AD6A1F66A0EEC414ED9");

	// ------------------------------ /
	//	getAllMailings				 /
	// -----------------------------/
	@Test
	public void canReadOrgUnitsMailingsWhenHavingPermission() throws Exception {
		String p = BASE + M_PATH + "/unit/" + TEST_OWNER_ID;
		mock.perform(get(p)
			.session(getPermittedSession())
			.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content").isArray())
		.andExpect(jsonPath("$.content", hasSize(3)));
	}
	
	@Test
	public void forbidsReadingMailsWithoutPermissionBeignOrgUnitMember() throws Exception {
		String p = BASE + M_PATH + "/unit/" + TEST_OWNER_ID;
		mock.perform(get(p)
			.session(getUnpermittedSession())
			.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isForbidden());
	}
	
	@Test
	public void returnsUnauthorizedForForeignOrgUnit() throws Exception {
		mock.perform(get(BASE + M_PATH + "/unit/" + TEST_OWNER_ID.toString())
				.session(getForeignSession())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	// ------------------------------ /
	//	createMailing				 /
	// -----------------------------/

	@Test
	public void canCreateMailingHavingPermission() throws Exception {
		MailingDTO d = new MailingDTO();
		d.setOwner(TEST_OWNER_ID);
		d.setReplyAddress("testaccount@art-ignition.de");
		d.setSenderAddress("testaccount@art-ignition.de");
		d.setSenderName("Max Mustermann");
		d.setSubject("Test CREATE subject");
		
		TemplateIndexDTO td = new TemplateIndexDTO();
		td.setId(UUID.fromString(TEST_TEMPLATE));
		d.setTemplate(td);
		
		mock.perform(post(URL + "/unit/" + TEST_OWNER_ID)
				.session(getPermittedSession())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(d)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.subject").value("Test CREATE subject"))
			.andExpect(jsonPath("$.ownerId").value(TEST_OWNER_ID))
			.andExpect(jsonPath("$.replyAddress").value("testaccount@art-ignition.de"))
			.andExpect(jsonPath("$.senderAddress").value("testaccount@art-ignition.de"))
			.andExpect(jsonPath("$.approvalRequested").value("false"))
			.andExpect(jsonPath("$.approved").value("false"))
			.andExpect(jsonPath("$.published").value("false"));
	}
	
	
	
	@Test
	public void canReadMailingByIdWhenHavingFullPermissions() throws Exception {
		mock.perform(get(URL + NEW_MAILING_ID)
				.session(getPermittedSession())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.id").value(NEW_MAILING_ID))
			.andExpect(jsonPath("$.subject").exists())
			.andExpect(jsonPath("$.subject").value("New Test Mailing"));
	}
	
	@Test
	public void forbidsReadMailingByIdWhenBeingMemberOfOrgUnitHavingNoPermissions() throws Exception {
		mock.perform(get(URL + NEW_MAILING_ID)
			.session(getUnpermittedSession())
			.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isForbidden());
	}
	
	@Test
	public void returnsUnauthorizedForForeignUserOnGetMailingById() throws Exception {
		mock.perform(get(URL + NEW_MAILING_ID)
			.session(getForeignSession())
			.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void canRequestApprovalHavingPermission() throws Exception {
		mock.perform(get(URL + NEW_MAILING_ID + "/requestApproval")
				.session(getPermittedSession())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		
		// fetch the mailing and see if it has been approved
		mock.perform(get(URL + NEW_MAILING_ID)
				.session(getPermittedSession())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.id").value(NEW_MAILING_ID))
			.andExpect(jsonPath("$.approvalRequested").exists())
			.andExpect(jsonPath("$.approvalRequested").value("true"))
			.andExpect(jsonPath("$.dateApprovalRequested").exists())
			.andExpect(jsonPath("$.userApprovalRequested").exists())
			.andExpect(jsonPath("$.userApprovalRequested.id").value(TEST_USER_FULL_PERMS));
	}

	@Test
	public void canApproveMailingWhenHavingPermission() throws Exception {
		mock.perform(put(BASE + M_PATH + "/" + AR_MAILING_ID + "/approve")
				.session(getPermittedSession())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	
	@Test
	public void failsApprovingMailWhenInsufficentPermissions() throws Exception {
		mock.perform(put(BASE + M_PATH + "/" + AR_MAILING_ID + "/approve")
				.session(getUnpermittedSession())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	/*
	@Test
	public void canPublishMailingWhenAuthorized() throws Exception {
		mock.perform(put(BASE + M_PATH + "/" + AP_MAILING_ID + "/publish")
				.session(getPermittedSession())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	*/
}
