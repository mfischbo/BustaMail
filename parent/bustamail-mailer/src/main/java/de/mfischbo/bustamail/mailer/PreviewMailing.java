package de.mfischbo.bustamail.mailer;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.mail.internet.InternetAddress;

import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;

public class PreviewMailing {

	private List<PersonalizedEmailRecipient>		recipients;

	private InternetAddress senderAddress;
	private String			senderName;
	
	private String			subject;
	private String 			htmlContent;
	private String			textContent;

	private URL				contentProviderBaseURL;
	
	private List<String>	removeClasses = new LinkedList<>();
	private List<String>	removeAttributes = new LinkedList<>();
	
	private String			disableLinkTrackClass;
	
	private boolean			spanCellReplacement = true;
	

	public PreviewMailing(String subject, String htmlContent, String textContent, PersonalizedEmailRecipient ... recipients) {
		this.subject = subject;
		this.htmlContent = htmlContent;
		this.textContent = textContent;
		this.recipients = Arrays.asList(recipients);
	}
	
	public void addRemoveClass(String classname) {
		this.removeClasses.add(classname);
	}
	
	public void addRemoveAttribute(String attr) {
		this.removeAttributes.add(attr);
	}

	public List<PersonalizedEmailRecipient> getRecipients() {
		return recipients;
	}

	public String getSubject() {
		return subject;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public String getTextContent() {
		return textContent;
	}

	public List<String> getRemoveClasses() {
		return removeClasses;
	}

	public List<String> getRemoveAttributes() {
		return removeAttributes;
	}

	public boolean isSpanCellReplacement() {
		return spanCellReplacement;
	}

	public URL getContentProviderBaseURL() {
		return contentProviderBaseURL;
	}

	public void setContentProviderBaseURL(URL contentProviderBaseURL) {
		this.contentProviderBaseURL = contentProviderBaseURL;
	}

	public String getDisableLinkTrackClass() {
		return disableLinkTrackClass;
	}

	public void setDisableLinkTrackClass(String disableLinkTrackClass) {
		this.disableLinkTrackClass = disableLinkTrackClass;
	}

	public InternetAddress getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(InternetAddress senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	public boolean isValid() {
		// check if content is set
		if (htmlContent == null && textContent == null)
			return false;
	
		if ((htmlContent != null && htmlContent.trim().length() == 0 || textContent != null && textContent.trim().length() == 0))
			return false;
		
		// check if we have at least one recipient with an email address
		if (this.recipients == null || this.recipients.size() == 0)
			return false;
		
		for (PersonalizedEmailRecipient r : this.recipients)
			if (r.getEmail() == null || r.getEmail().trim().length() == 0)
				return false;
		
		if (contentProviderBaseURL == null)
			return false;
		
		return true;
	}
}
