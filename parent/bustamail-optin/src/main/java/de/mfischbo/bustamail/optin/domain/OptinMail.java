package de.mfischbo.bustamail.optin.domain;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;
import de.mfischbo.bustamail.mailer.dto.IMailableMail;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.template.domain.TemplatePack;

@Document(collection = "Optin_OptinMail")
public class OptinMail extends OwnedBaseDomain implements IMailableMail {

	private static final long serialVersionUID = 6647303761639662254L;

	public enum ActivationMode {
		INTERNAL,		// link is pointing to API Endpoint, subscriber being forwarded
		EXTERNAL		// link points to 3rd party site, activation via client
	}
	
	@NotBlank
	private String 		name;
	
	@NotBlank
	private String		senderAddress;
	
	@NotBlank
	private String		replyAddress;
	
	@NotBlank
	private String		senderName;
	
	private boolean		activated;

	@NotBlank
	private String		subject;
	
	@DBRef
	private TemplatePack templatePack;
	
	private ObjectId	templateId;
	
	private DateTime	dateCreated;
	
	private DateTime	dateModified;
	
	@DBRef
	private User		userCreated;
	
	@DBRef
	private User		userModified;
	
	private ActivationMode	activationMode;
	
	private String		targetURL;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getReplyAddress() {
		return replyAddress;
	}

	public void setReplyAddress(String replyAddress) {
		this.replyAddress = replyAddress;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public TemplatePack getTemplatePack() {
		return templatePack;
	}

	public void setTemplatePack(TemplatePack templatePack) {
		this.templatePack = templatePack;
	}

	public ObjectId getTemplateId() {
		return templateId;
	}

	public void setTemplateId(ObjectId templateId) {
		this.templateId = templateId;
	}

	public User getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(User userCreated) {
		this.userCreated = userCreated;
	}

	public User getUserModified() {
		return userModified;
	}

	public void setUserModified(User userModified) {
		this.userModified = userModified;
	}

	public DateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public DateTime getDateModified() {
		return dateModified;
	}

	public void setDateModified(DateTime dateModified) {
		this.dateModified = dateModified;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public ActivationMode getActivationMode() {
		return activationMode;
	}

	public void setActivationMode(ActivationMode activationMode) {
		this.activationMode = activationMode;
	}

	public String getTargetURL() {
		return targetURL;
	}

	public void setTargetURL(String targetURL) {
		this.targetURL = targetURL;
	}

	@Override
	@JsonIgnore
	public InternetAddress getSender() {
		try {
			return new InternetAddress(senderAddress);
		} catch (AddressException ex) {
			throw new IllegalStateException(ex.getMessage());
		}
	}

	@Override
	@JsonIgnore
	public InternetAddress getReplyTo() {
		try {
			return new InternetAddress(replyAddress);
		} catch (AddressException ex) {
			throw new IllegalStateException(ex.getMessage());
		}
	}
}
