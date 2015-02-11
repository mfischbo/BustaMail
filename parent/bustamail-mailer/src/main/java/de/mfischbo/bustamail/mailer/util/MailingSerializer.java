package de.mfischbo.bustamail.mailer.util;

import java.io.File;

import javax.inject.Inject;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;
import de.mfischbo.bustamail.mailer.dto.SerializedMailing;

/**
 * This class serializes a mailing into multiple files (one for each recipient).
 * 
 * @author M.Fischboeck
 *
 */
@Component
public class MailingSerializer {

	@Inject
	private	ObjectMapper			mapper;	
	
	private boolean					isDevMode;
	private String					testAddress;
	
	public static final String		KEY_MAILING_ID = "mailingId";
	public static final String		KEY_SUBSCRIBER_ID = "subscriberId";
	
	@Inject
	public MailingSerializer(Environment env) {
		this.isDevMode = true;
		this.testAddress = env.getProperty("de.mfischbo.bustamail.mailingSerializer.defaultDevAddress");
		
		String[] profiles = env.getActiveProfiles();
		for (String profile : profiles) {
			if (profile.equals("production")) {
				isDevMode = false;
			}
		}
	}
	
	public boolean serializeMailing(File jobFolder, LiveMailing lm, String personalizedHtml, String personalizedText, PersonalizedEmailRecipient r) {
	
		try {
			String fName = jobFolder.getAbsolutePath() + "/" + r.getId();
			SerializedMailing m = new SerializedMailing();
			
			m.setSubject(lm.getSubject());
			m.setSenderAddress(lm.getSenderAddress().toString());
			m.setReplyToAddress(lm.getReplyToAddress().toString());
			m.setSenderName(lm.getSenderName());
			
			m.setRecipientAddress(r.getEmail());
			if (isDevMode)
				m.setRecipientAddress(testAddress);
			
			m.setHtmlContent(personalizedHtml);
			m.setTextContent(personalizedText);

			mapper.writeValue(new File(fName), m);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
}