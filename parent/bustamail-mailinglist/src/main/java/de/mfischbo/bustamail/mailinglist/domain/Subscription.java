package de.mfischbo.bustamail.mailinglist.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;

@Entity
@Table(name = "SubscriptionList_Subscription", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"SubscriptionList_id","EMailAddress_id"})
})
public class Subscription extends BaseDomain {

	private static final long serialVersionUID = 3316987035776229064L;

	public enum SourceType {
		UserAction,
		ImportAction,
		FormAction
	}
	
	public enum State {
		ACTIVE,
		OPTIN,
		INACTIVE
	}
	
	@Basic(optional = false)
	private DateTime				dateCreated;

	@Basic(optional = false)
	@Enumerated(EnumType.STRING)
	private SourceType				sourceType;
	
	@Basic
	@Enumerated(EnumType.STRING)
	private State					state;
	
	@Basic(optional = true)
	private String					ipAddress;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "SubscriptionList_id", referencedColumnName = "id")
	private	SubscriptionList		subscriptionList;

	
	@OneToOne(optional = false)
	@JoinColumn(name = "EMailAddress_id", referencedColumnName = "id")
	private EMailAddress			emailAddress;
	
	
	public DateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public SourceType getSourceType() {
		return sourceType;
	}

	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public SubscriptionList getSubscriptionList() {
		return subscriptionList;
	}

	public void setSubscriptionList(SubscriptionList subscriptionList) {
		this.subscriptionList = subscriptionList;
	}

	public EMailAddress getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(EMailAddress emailAddress) {
		this.emailAddress = emailAddress;
	}
}
