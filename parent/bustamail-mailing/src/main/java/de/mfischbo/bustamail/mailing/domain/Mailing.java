package de.mfischbo.bustamail.mailing.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;
import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.template.domain.Template;

@Entity
@Table(name = "Mailing_Mailing")
public class Mailing extends OwnedBaseDomain {

	@Basic
	@NotBlank
	private String 		subject;
	
	@Basic
	@Email
	@NotBlank
	private String 		senderAddress;
	
	@Basic
	@NotBlank
	private String 		senderName;
	
	@Basic
	@Email
	@NotBlank
	private String 		replyAddress;

	@ManyToOne
	@JoinColumn(name = "Template_id", referencedColumnName = "id", nullable = false)
	private Template 	template;
	
	@ManyToOne
	@JoinColumn(name = "User_Created_id", referencedColumnName = "id", nullable = false)
	private User		userCreated;
	
	@ManyToOne
	@JoinColumn(name = "User_Modified_id", referencedColumnName = "id", nullable = false)
	private User		userModified;

	@ManyToOne
	@JoinColumn(name = "User_Approval_Requested_id", referencedColumnName = "id", nullable = true)
	private User		userApprovalRequestd;
	
	@ManyToOne
	@JoinColumn(name = "User_Approved_id", referencedColumnName = "id", nullable = true)
	private User		userApproved;

	@ManyToOne
	@JoinColumn(name = "User_Published_id", referencedColumnName = "id", nullable = true)
	private User		userPublished;

	@Basic
	private DateTime	dateCreated;

	@Basic
	private DateTime	dateModified;

	@Basic
	private DateTime	dateApprovalRequested;
	
	@Basic
	private DateTime	dateApproved;

	@Basic
	private DateTime	datePublished;

	@Basic
	private boolean		approvalRequested;
	
	@Basic
	private boolean		approved;

	@Basic
	private boolean		published;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getReplyAddress() {
		return replyAddress;
	}

	public void setReplyAddress(String replyAddress) {
		this.replyAddress = replyAddress;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
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

	public User getUserApprovalRequestd() {
		return userApprovalRequestd;
	}

	public void setUserApprovalRequestd(User userApprovalRequestd) {
		this.userApprovalRequestd = userApprovalRequestd;
	}

	public User getUserApproved() {
		return userApproved;
	}

	public void setUserApproved(User userApproved) {
		this.userApproved = userApproved;
	}

	public User getUserPublished() {
		return userPublished;
	}

	public void setUserPublished(User userPublished) {
		this.userPublished = userPublished;
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

	public DateTime getDateApprovalRequested() {
		return dateApprovalRequested;
	}

	public void setDateApprovalRequested(DateTime dateApprovalRequested) {
		this.dateApprovalRequested = dateApprovalRequested;
	}

	public DateTime getDateApproved() {
		return dateApproved;
	}

	public void setDateApproved(DateTime dateApproved) {
		this.dateApproved = dateApproved;
	}

	public DateTime getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(DateTime datePublished) {
		this.datePublished = datePublished;
	}

	public boolean isApprovalRequested() {
		return approvalRequested;
	}

	public void setApprovalRequested(boolean approvalRequested) {
		this.approvalRequested = approvalRequested;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}
}
