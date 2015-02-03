package de.mfischbo.bustamail.mailer.dto;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.bson.types.ObjectId;

import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;

/**
 * This class provides a model for a Live Mailing that is going to be published.
 * Besides the data that is inevitable for a e-mail it provides configuration flags on how the mailings
 * contents will be parsed.
 * @author M. Fischboeck 
 *
 */
public class LiveMailing {

	/* The id of the mailing that will be sent */
	private ObjectId								mailingId;
	
	/* The set of recipients for this mailing */
	private Set<PersonalizedEmailRecipient>			recipients;
	
	/* The senders address */
	private InternetAddress							senderAddress;
	
	/* The reply to address */
	private InternetAddress							replyToAddress;
	
	/* The senders name (e.g. John Doe) */
	private String									senderName;
	
	/* The subject of the mailing */
	private String									subject;
	
	/* The mailings HTML body part */
	private String									htmlContent;
	
	/* The mailings text body part */
	private String									textContent;
	
	/* The base url from where to retrieve resources and contents (e.g. Images) */
	private URL										contentProviderBaseURL;

	/* The list of html/css class names that will be removed from any node of the HTML */
	private List<String>							removeClasses = new LinkedList<>();
	
	/* The list of html attributes that will be removed from any node of the HTML */
	private List<String>							removeAttributes = new LinkedList<>();

	/* The html CSS class that indicates that an <a href> will not be tracked */
	private String									disableLinkTrackClass;
	
	/* Flag, indicating if empty td's should be filled with a blank.gif image */
	private boolean									spanCellReplacement = true;
	
	/* Flag, indicating if an opening of the mailing should be tracked (e.g. by tracking pixel) */
	private boolean									enableOpeningTracking = true;
	
	/* Flag indicating if global link tracking is enabled for the mailing */
	private boolean									enableLinkTracking = true;
	
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

	public void setMailingId(ObjectId mailingId) {
		this.mailingId = mailingId;
	}

	public Set<PersonalizedEmailRecipient> getRecipients() {
		return recipients;
	}

	public void setRecipients(Set<PersonalizedEmailRecipient> recipients) {
		this.recipients = recipients;
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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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

	public URL getContentProviderBaseURL() {
		return contentProviderBaseURL;
	}

	public void setContentProviderBaseURL(URL contentProviderBaseURL) {
		this.contentProviderBaseURL = contentProviderBaseURL;
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

	public boolean isEnableOpeningTracking() {
		return enableOpeningTracking;
	}

	public void setEnableOpeningTracking(boolean enableOpeningTracking) {
		this.enableOpeningTracking = enableOpeningTracking;
	}

	public boolean isEnableLinkTracking() {
		return enableLinkTracking;
	}

	public void setEnableLinkTracking(boolean enableLinkTracking) {
		this.enableLinkTracking = enableLinkTracking;
	}
}
