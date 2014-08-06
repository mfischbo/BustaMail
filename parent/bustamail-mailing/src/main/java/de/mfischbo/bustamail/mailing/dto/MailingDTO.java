package de.mfischbo.bustamail.mailing.dto;

import java.util.List;

import de.mfischbo.bustamail.mailinglist.dto.SubscriptionListDTO;
import de.mfischbo.bustamail.security.dto.UserDTO;
import de.mfischbo.bustamail.vc.dto.VersionedContentDTO;

public class MailingDTO extends MailingIndexDTO {

	private static final long serialVersionUID = 408241813756372838L;

	private VersionedContentDTO			htmlContent;
	private VersionedContentDTO			textContent;
	
	private UserDTO						userModified;
	private UserDTO						userPublished;
	private List<SubscriptionListDTO>	subscriptionLists;
	
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
	public UserDTO getUserModified() {
		return userModified;
	}
	public void setUserModified(UserDTO userModified) {
		this.userModified = userModified;
	}
	public UserDTO getUserPublished() {
		return userPublished;
	}
	public void setUserPublished(UserDTO userPublished) {
		this.userPublished = userPublished;
	}
	public List<SubscriptionListDTO> getSubscriptionLists() {
		return subscriptionLists;
	}
	public void setSubscriptionLists(List<SubscriptionListDTO> subscriptionLists) {
		this.subscriptionLists = subscriptionLists;
	}
}
