package de.mfischbo.bustamail.landingpage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.landingpage.domain.LPForm;
import de.mfischbo.bustamail.landingpage.domain.LPFormEntry;
import de.mfischbo.bustamail.landingpage.domain.LPFormSubmission;
import de.mfischbo.bustamail.landingpage.dto.ValidationError;
import de.mfischbo.bustamail.landingpage.dto.ValidationError.ErrorType;
import de.mfischbo.bustamail.mailer.service.SimpleMailService;
import de.mfischbo.bustamail.template.service.TemplateService;

@Service
public class LandingPageFormServiceImpl extends BaseService implements FormSubmissionService {

	@Inject
	SimpleMailService		mailService;
	
	@Inject
	TemplateService			templateService;
	
	@Override
	public List<ValidationError> processFormSubmission(LPForm form,
			Map<LPFormEntry, String> values, String sourceAddress) {
		
		List<ValidationError> errors = validate(values);
		if (errors.size() > 0)
			return errors;
		
		
		if (form.isTriggersMail())
			sendFormSubmissionMail(form, values);
		
		return new ArrayList<ValidationError>();
	}

	
	private List<ValidationError> validate(Map<LPFormEntry, String> map) {
		List<ValidationError> retval = new ArrayList<>(map.size());
		
		for (LPFormEntry e : map.keySet()) {

			if (e.isRequired() && (map.get(e) == null || map.get(e).isEmpty())) {
				retval.add(new ValidationError(e.getName(), ErrorType.MISSING_REQUIRED));
				continue;
			}
			
			switch (e.getValidationType()) {
			case EMAIL:
				System.out.println("Do something");
			case DATE:
				System.out.println("Validate date");
			case REGEXP:
				System.out.println("Regex validation");
			case INTEGER:
				System.out.println("Integer validation");
			case FLOAT:
				System.out.println("Float validation");
			}
		}
		return retval;
	}
	
	private void createFormSubmission(LPForm form, String sourceIp) {
		// on success create a form submission and store it
		LPFormSubmission sub = new LPFormSubmission();
		sub.setForm(form);
		sub.setSourceIP(sourceIp);
		//sub.setData(mapper.writeValueAsString(request.getParameterMap()));
		//service.createFormSubmission(sub);
	}

	private void sendFormSubmissionMail(LPForm form, Map<LPFormEntry, String> values) {
		/*
		if (!form.isTriggersMail())
			return;
		
		//Template t = form.getMailTemplate();
		TemplatePack tp = templateService.getTemplatePackContainingTemplateById(form.getMailTemplateId());
		Template t = null;
		for (Template tx : tp.getTemplates())
			if (tx.getId().equals(form.getMailTemplateId()))
				t = tx;
		
		for (String r : form.getRecipients()) {
			try {
				mailService.sendSimpleHtmlMail(new InternetAddress("noreply@fischboeck.net"), 
					"Landing Page Service", "noreply@fischboeck.net", new InternetAddress(r), 
					"Landing Page Formular", t.getSource());
			} catch (Exception ex) {
				log.error("Failed to send form submission mail to : " + r);
			}
		}
		*/
	}
}
