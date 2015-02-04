package de.mfischbo.bustamail.stats.domain;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mfischbo.bustamail.common.domain.BaseDomain;

@Document(collection = "Stats_StatsEntry")
public class StatsEntry extends BaseDomain {

	private static final long serialVersionUID = 1858158036121028933L;

	public enum RecordType {
		OPEN,				// indicates a mailing has been opened by the subscriber
		CLICK,				// indicates a link in the mailing has been clicked by the subscriber
		SENT_SUCCESS,		// indicates the mailing has been submitted to the destination MTA
		SENT_FAILURE		// indicates the mailing has been rejected by the destination MTA or dest. MTA was unreachable (hardbounce)
	}
	
	private ObjectId	mailingId;
	
	private ObjectId	subscriptionId;

	private RecordType	type;
	
	private String		targetUrl;
	
	private String		sourceIP;

	private	DateTime	dateCreated;

	public StatsEntry() {
		this.dateCreated = DateTime.now();
	}

	public ObjectId getMailingId() {
		return mailingId;
	}

	public void setMailingId(ObjectId mailingId) {
		this.mailingId = mailingId;
	}

	public ObjectId getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(ObjectId subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public RecordType getType() {
		return type;
	}

	public void setType(RecordType type) {
		this.type = type;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public DateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getSourceIP() {
		return sourceIP;
	}

	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}
}
