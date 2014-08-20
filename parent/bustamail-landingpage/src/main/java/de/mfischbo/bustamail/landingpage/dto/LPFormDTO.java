package de.mfischbo.bustamail.landingpage.dto;

import java.util.List;

import de.mfischbo.bustamail.common.dto.BaseDTO;
import de.mfischbo.bustamail.landingpage.domain.LPForm.SubmissionAction;

public class LPFormDTO extends BaseDTO {
	
	private static final long serialVersionUID = -7994664424797975283L;

	private String 				name;
	private SubmissionAction	onSuccessAction;
	private String				redirectTarget;
	private boolean				isConversion;
	private List<LPFormEntryDTO>	fields;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SubmissionAction getOnSuccessAction() {
		return onSuccessAction;
	}
	public void setOnSuccessAction(SubmissionAction onSuccessAction) {
		this.onSuccessAction = onSuccessAction;
	}
	public String getRedirectTarget() {
		return redirectTarget;
	}
	public void setRedirectTarget(String redirectTarget) {
		this.redirectTarget = redirectTarget;
	}
	public boolean isConversion() {
		return isConversion;
	}
	public void setConversion(boolean isConversion) {
		this.isConversion = isConversion;
	}
	public List<LPFormEntryDTO> getFields() {
		return fields;
	}
	public void setFields(List<LPFormEntryDTO> fields) {
		this.fields = fields;
	}
}
