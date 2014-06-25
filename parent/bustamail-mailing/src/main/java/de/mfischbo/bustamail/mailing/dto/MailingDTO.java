package de.mfischbo.bustamail.mailing.dto;

import de.mfischbo.bustamail.security.dto.UserDTO;

public class MailingDTO extends MailingIndexDTO {

	private static final long serialVersionUID = 408241813756372838L;

	private VersionedContentDTO			htmlContent;
	private VersionedContentDTO			textContent;
	
	private UserDTO						userCreated;
	private UserDTO						userModified;
	private UserDTO						userApprovalRequested;
	private UserDTO						userApproved;
	private UserDTO						userPublished;
	public VersionedContentDTO getHtmlContent() {
		return htmlContent;
	}
	public void setHtmlContent(VersionedContentDTO htmlContent) {
		this.htmlContent = htmlContent;
	}
	public VersionedContentDTO getTextContent() {
		return textContent;
	}
	public void setTextContent(VersionedContentDTO textContent) {
		this.textContent = textContent;
	}
	public UserDTO getUserCreated() {
		return userCreated;
	}
	public void setUserCreated(UserDTO userCreated) {
		this.userCreated = userCreated;
	}
	public UserDTO getUserModified() {
		return userModified;
	}
	public void setUserModified(UserDTO userModified) {
		this.userModified = userModified;
	}
	public UserDTO getUserApprovalRequested() {
		return userApprovalRequested;
	}
	public void setUserApprovalRequested(UserDTO userApprovalRequested) {
		this.userApprovalRequested = userApprovalRequested;
	}
	public UserDTO getUserApproved() {
		return userApproved;
	}
	public void setUserApproved(UserDTO userApproved) {
		this.userApproved = userApproved;
	}
	public UserDTO getUserPublished() {
		return userPublished;
	}
	public void setUserPublished(UserDTO userPublished) {
		this.userPublished = userPublished;
	}
}
