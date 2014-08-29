package de.mfischbo.bustamail.landingpage.web;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mfischbo.bustamail.landingpage.domain.LPForm;
import de.mfischbo.bustamail.landingpage.domain.LPFormEntry;
import de.mfischbo.bustamail.landingpage.domain.LPFormSubmission;
import de.mfischbo.bustamail.landingpage.service.LandingPageService;

@Controller
@RequestMapping("/forms")
public class LPFormWebController {

	@Inject
	private LandingPageService		service;
	
	@Inject
	private ObjectMapper			mapper;

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public void processForm(@PathVariable("id") UUID formId, HttpServletRequest request, HttpServletResponse response) {

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
		
			// validate for required
			List<LPFormEntry> requiredEmpties = getRequiredUnsubmittedFields(fieldVals);
			
		
			// on success create a form submission and store it
			LPFormSubmission sub = new LPFormSubmission();
			sub.setForm(form);
			sub.setSourceIP(request.getRemoteAddr());
			sub.setData(mapper.writeValueAsString(request.getParameterMap()));
			service.createFormSubmission(sub);
			
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
	
	private List<LPFormEntry> getRequiredUnsubmittedFields(Map<LPFormEntry, String> map) {
		
		List<LPFormEntry> retval = new ArrayList<>(map.size());
		for (LPFormEntry e : map.keySet()) {
			if (e.isRequired() && map.get(e).isEmpty())
				retval.add(e);
		}
		return retval;
	}
	
	
	private List<LPFormEntry> validate(Map<LPFormEntry, String> map) {
		List<LPFormEntry> retval = new ArrayList<>(map.size());
		
		for (LPFormEntry e : map.keySet()) {
		
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
}
