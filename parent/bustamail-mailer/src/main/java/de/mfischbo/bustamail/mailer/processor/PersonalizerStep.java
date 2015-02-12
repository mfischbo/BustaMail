package de.mfischbo.bustamail.mailer.processor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;
import de.mfischbo.bustamail.mailer.util.MailingSerializer;
import freemarker.template.Configuration;
import freemarker.template.Template;

@Component
public class PersonalizerStep {

	@Inject FreeMarkerConfigurer	fmCfger;
	
	public String processHTML(LiveMailing m, PersonalizedEmailRecipient r) {
		try {
			Map<String, Object> model = new HashMap<>();
			model.put(MailingSerializer.KEY_MAILING_ID, m.getMailingId().toHexString());
			model.put(MailingSerializer.KEY_SUBSCRIBER_ID, r.getId().toHexString());
			
			model.put("gender", r.getGender().toString());
			model.put("firstName", r.getFirstName());
			model.put("lastName", r.getLastName());
			model.put("title", r.getTitle());
			model.put("formal", r.hasFormalSalutation());
			model.put("email", r.getEmail());
			model.put("mailingData", m.getMailingData()); // reserved for custom data
		
			Configuration cfg = fmCfger.getConfiguration();
			
			Template ht = new Template("htDummy", m.getHtmlContent(), cfg);
			String retval = FreeMarkerTemplateUtils.processTemplateIntoString(ht, model);
			cfg.clearSharedVariables();
			return retval;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public String processText(LiveMailing m, PersonalizedEmailRecipient r) {
		return m.getTextContent();
	}
}
