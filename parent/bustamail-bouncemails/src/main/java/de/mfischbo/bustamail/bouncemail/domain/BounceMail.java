package de.mfischbo.bustamail.bouncemail.domain;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mfischbo.bustamail.common.domain.BaseDomain;

@Document(collection = "Bounce_BounceMail")
public class BounceMail extends BaseDomain {

	private static final long serialVersionUID = 5174801081999462493L;

	@Indexed
	private ObjectId		accountId;

	@Indexed
	private String			messageId;
	
	private String			subject;

	private DateTime		dateCreated;
	private DateTime		dateSent;
	private DateTime		dateReceived;
	
	private String			sender;
	private List<String>	from = new ArrayList<>();
	
	public ObjectId getAccountId() {
		return accountId;
	}

	public void setAccountId(ObjectId accountId) {
		this.accountId = accountId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	

	public DateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public DateTime getDateSent() {
		return dateSent;
	}

	public void setDateSent(DateTime dateSent) {
		this.dateSent = dateSent;
	}

	public DateTime getDateReceived() {
		return dateReceived;
	}

	public void setDateReceived(DateTime dateReceived) {
		this.dateReceived = dateReceived;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public List<String> getFrom() {
		return from;
	}

	public void setFrom(List<String> from) {
		this.from = from;
	}
}
