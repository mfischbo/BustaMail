package de.mfischbo.bustamail.landingpage.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.mfischbo.bustamail.landingpage.domain.LPForm;
import de.mfischbo.bustamail.landingpage.domain.LPFormEntry;
import de.mfischbo.bustamail.landingpage.dto.ValidationError;
import de.mfischbo.bustamail.landingpage.service.FormSubmissionService;
import de.mfischbo.bustamail.landingpage.service.LandingPageService;

@Controller
@RequestMapping("/forms")
public class LPFormWebController {

	@Inject
	private LandingPageService		service;
	
	@Inject
	private FormSubmissionService	formService;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public void processForm(@PathVariable("id") ObjectId formId, HttpServletRequest request, HttpServletResponse response) {

		try {
			LPForm form = service.getFormById(formId);
		
			// collect all form fields
			Map<LPFormEntry, String> fieldVals = new HashMap<>(); 
			for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
				String pName = e.nextElement();
				String value = request.getParameter(pName);
				LPFormEntry entry = getEntryForName(form, pName);
				if (entry != null)
					fieldVals.put(entry, value);
			}
			List<ValidationError> errors = formService.processFormSubmission(form, fieldVals, request.getRemoteAddr());
			if (errors.size() == 0) {
				// redirect or so
			}
		
		
		
		
			// what to do after successfull processing?
		
			
		} catch (Exception ex) {
			log.error("Caught exception processing form. Cause: " + ex.getMessage());
			// well, thats bad, but we ain't tellin' nobody ... psssst!
		}
	}
	
	private LPFormEntry getEntryForName(LPForm form, String name) {
		for (LPFormEntry e : form.getFields()) {
			if (e.getName().equalsIgnoreCase(name)) 
				return e;
		}
		return null;
	}
}
