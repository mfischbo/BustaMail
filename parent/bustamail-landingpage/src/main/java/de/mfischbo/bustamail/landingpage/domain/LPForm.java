package de.mfischbo.bustamail.landingpage.domain;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.template.domain.Template;

@Entity
@Table(name = "LandingPage_LPForm")
public class LPForm extends BaseDomain {

	public enum SubmissionAction {
		NONE,
		REDIRECT
	}
	
	private static final long serialVersionUID = 6429724356594167395L;

	@NotBlank
	@Basic(optional = false)
	private String name;
	
	@ElementCollection
	@CollectionTable(
			name = "LandingPage_LPFormEntry",
			joinColumns = @JoinColumn(name = "Form_id")
	)
	private List<LPFormEntry>	fields;

	// the action that should be performed on successful submission
	@Basic(optional = false)
	@NotNull
	@Enumerated(EnumType.STRING)
	private SubmissionAction	onSuccessAction;

	// a URL that points to the page the user is beign redirected after 
	// successfull submission
	@Basic
	@Column(length = 4096)
	private String				redirectTarget;

	// states if a successful submission should be counted as conversion
	@Basic
	private boolean				isConversion;
	
	// states if a submission triggers a mail
	@Basic
	private boolean				triggersMail;
	
	@ElementCollection
	private List<String>		recipients;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "MailTemplate_id", referencedColumnName = "id", nullable = true)
	private Template			mailTemplate;

	// the landing page the form belongs to
	@ManyToOne(optional = false)
	@JoinColumn(name = "LandingPage_id", referencedColumnName = "id", nullable = false)
	private LandingPage			landingPage;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<LPFormEntry> getFields() {
		return fields;
	}

	public void setFields(List<LPFormEntry> fields) {
		this.fields = fields;
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

	public LandingPage getLandingPage() {
		return landingPage;
	}

	public void setLandingPage(LandingPage landingPage) {
		this.landingPage = landingPage;
	}

	public boolean isTriggersMail() {
		return triggersMail;
	}

	public void setTriggersMail(boolean triggersMail) {
		this.triggersMail = triggersMail;
	}
	
	public List<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}

	public Template getMailTemplate() {
		return mailTemplate;
	}

	public void setMailTemplate(Template mailTemplate) {
		this.mailTemplate = mailTemplate;
	}
}
