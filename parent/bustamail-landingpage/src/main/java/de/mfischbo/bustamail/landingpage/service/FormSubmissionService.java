package de.mfischbo.bustamail.landingpage.service;

import java.util.List;
import java.util.Map;

import de.mfischbo.bustamail.landingpage.domain.LPForm;
import de.mfischbo.bustamail.landingpage.domain.LPFormEntry;
import de.mfischbo.bustamail.landingpage.dto.ValidationError;

public interface FormSubmissionService {

	public List<ValidationError> processFormSubmission(LPForm form, Map<LPFormEntry, String> values, String remoteIp);
}
