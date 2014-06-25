package de.mfischbo.bustamail.mailing.dto;

import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.dto.OwnedBaseDTO;
import de.mfischbo.bustamail.template.dto.TemplateIndexDTO;

public class MailingIndexDTO extends OwnedBaseDTO {

	private static final long serialVersionUID = -7364058323071394610L;
	
	private String				subject;
	private String				senderAddress;
	private String				senderName;
	private String				replyAddress;
	private TemplateIndexDTO	template;
	private DateTime			dateCreated;
	private DateTime			dateModified;
	private DateTime			dateApprovalRequested;
	private DateTime			dateApproved;
	private DateTime			datePublished;
	
	private boolean				approvalRequested;
	private boolean				approved;
	private boolean				published;
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
	public TemplateIndexDTO getTemplate() {
		return template;
	}
	public void setTemplate(TemplateIndexDTO template) {
		this.template = template;
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
