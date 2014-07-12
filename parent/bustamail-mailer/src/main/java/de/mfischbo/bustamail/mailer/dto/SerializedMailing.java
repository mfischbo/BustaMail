package de.mfischbo.bustamail.mailer.dto;

import java.io.Serializable;

public class SerializedMailing implements Serializable {

	private static final long serialVersionUID = -4251777535899756965L;
	
	private String		subject;
	private String		senderAddress;
	private String		replyToAddress;
	private String		senderName;
	private String		htmlContent;
	private String		textContent;
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
	public String getReplyToAddress() {
		return replyToAddress;
	}
	public void setReplyToAddress(String replyToAddress) {
		this.replyToAddress = replyToAddress;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getHtmlContent() {
		return htmlContent;
	}
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
	public String getTextContent() {
		return textContent;
	}
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
}
