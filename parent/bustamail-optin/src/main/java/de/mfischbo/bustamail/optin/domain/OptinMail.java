package de.mfischbo.bustamail.optin.domain;

import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.template.domain.TemplatePack;

@Document(collection = "Optin_OptinMail")
public class OptinMail extends OwnedBaseDomain {

	private static final long serialVersionUID = 6647303761639662254L;

	@NotBlank
	private String 		name;
	
	@NotBlank
	private String		senderAddress;
	
	@NotBlank
	private String		replyAddress;

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
	
	
}
