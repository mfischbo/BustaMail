package de.mfischbo.bustamail.mailer.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;
import de.mfischbo.bustamail.mailer.dto.SerializedMailing;
import freemarker.template.Template;

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
	
	@Inject
	private FreeMarkerConfigurer	fmCfger;
	
	private boolean					isDevMode;
	private String					testAddress;
	
	static final String		KEY_MAILING_ID = "mailingId";
	static final String		KEY_SUBSCRIBER_ID = "subscriberId";
	
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
	
	public boolean serializeMailing(File jobFolder, LiveMailing lm, PersonalizedEmailRecipient r) {
	
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
		
			
			Map<String, String> model = new HashMap<>();
			model.put(KEY_MAILING_ID, lm.getMailingId().toHexString());
			model.put(KEY_SUBSCRIBER_ID, r.getId().toHexString());
		
			
			Template ht = new Template("htDummy", lm.getHtmlContent(), fmCfger.getConfiguration());
			m.setHtmlContent(FreeMarkerTemplateUtils.processTemplateIntoString(ht, model));
			m.setTextContent(lm.getTextContent());
		
			/*
			Template tt = new Template("txDummy", preparedText, fmCfger.getConfiguration());
			m.setTextContent(FreeMarkerTemplateUtils.processTemplateIntoString(tt, r));
			*/
			
			mapper.writeValue(new File(fName), m);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}