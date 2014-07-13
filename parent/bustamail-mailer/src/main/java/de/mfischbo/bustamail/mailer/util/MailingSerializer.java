package de.mfischbo.bustamail.mailer.util;

import java.io.File;

import javax.inject.Inject;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;
import de.mfischbo.bustamail.mailer.dto.SerializedMailing;

@Component
public class MailingSerializer {

	@Inject
	private	ObjectMapper			mapper;	
	
	@Inject
	private FreeMarkerConfigurer	fmCfger;
	
	private boolean					isDevMode;
	private String					testAddress;
	
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
	
	public boolean serializeMailing(File jobFolder, LiveMailing lm, String preparedHTML, String preparedText, PersonalizedEmailRecipient r) {
		
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

			/*
			Template ht = new Template("htDummy", preparedHTML, fmCfger.getConfiguration());
			m.setHtmlContent(FreeMarkerTemplateUtils.processTemplateIntoString(ht, r));
			
			Template tt = new Template("txDummy", preparedText, fmCfger.getConfiguration());
			m.setTextContent(FreeMarkerTemplateUtils.processTemplateIntoString(tt, r));
			*/
			m.setTextContent(preparedText);
			m.setHtmlContent(preparedHTML);
	
			mapper.writeValue(new File(fName), m);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}