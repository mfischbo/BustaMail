package de.mfischbo.bustamail.mailer.dto;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.bson.types.ObjectId;

import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;

public class LiveMailing {

	private ObjectId								mailingId;
	private Set<PersonalizedEmailRecipient>			recipients;
	private InternetAddress							senderAddress;
	private InternetAddress							replyToAddress;
	private String									senderName;
	private String									subject;
	private String									htmlContent;
	private String									textContent;
	private URL										contentProviderBaseURL;
	
	private List<String>							removeClasses = new LinkedList<>();
	private List<String>							removeAttributes = new LinkedList<>();
	
	private String									disableLinkTrackClass;
	private boolean									spanCellReplacement = true;
	
	public LiveMailing(ObjectId mailingId, String subject, String htmlContent, URL contentBaseUrl) {
		this.mailingId = mailingId;
		this.subject = subject;
		this.htmlContent = htmlContent;
		this.contentProviderBaseURL = contentBaseUrl;
	}
	
	public LiveMailing(ObjectId mailingId, String subject, String htmlContent, String textContent, URL contentBaseUrl) {
		this.mailingId = mailingId;
		this.subject = subject;
		this.htmlContent = htmlContent;
		this.textContent = textContent;
		this.contentProviderBaseURL = contentBaseUrl;
	}
	
	public ObjectId getMailingId() {
		return mailingId;
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

	public URL getContentProviderBaseURL() {
		return contentProviderBaseURL;
	}

	public void setRecipients(Set<PersonalizedEmailRecipient> recipients) {
		this.recipients = recipients;
	}
	
	public Set<PersonalizedEmailRecipient> getRecipients() {
		return this.recipients;
	}

	public List<String> getRemoveClasses() {
		return removeClasses;
	}

	public void setRemoveClasses(List<String> removeClasses) {
		this.removeClasses = removeClasses;
	}

	public List<String> getRemoveAttributes() {
		return removeAttributes;
	}

	public void setRemoveAttributes(List<String> removeAttributes) {
		this.removeAttributes = removeAttributes;
	}

	public String getDisableLinkTrackClass() {
		return disableLinkTrackClass;
	}

	public void setDisableLinkTrackClass(String disableLinkTrackClass) {
		this.disableLinkTrackClass = disableLinkTrackClass;
	}

	public boolean isSpanCellReplacement() {
		return spanCellReplacement;
	}

	public void setSpanCellReplacement(boolean spanCellReplacement) {
		this.spanCellReplacement = spanCellReplacement;
	}

	public InternetAddress getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(InternetAddress senderAddress) {
		this.senderAddress = senderAddress;
	}

	public InternetAddress getReplyToAddress() {
		return replyToAddress;
	}

	public void setReplyToAddress(InternetAddress replyToAddress) {
		this.replyToAddress = replyToAddress;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
}
