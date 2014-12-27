package de.mfischbo.bustamail.mailing.domain;

import java.util.List;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.security.domain.User;

@Document(collection = "Mailing")
public class Mailing extends OwnedBaseDomain {

	private static final long serialVersionUID = 4233998675412535889L;

	@NotBlank
	private String 		subject;
	
	@Email
	@NotBlank
	private String 		senderAddress;
	
	@NotBlank
	private String 		senderName;
	
	@Email
	@NotBlank
	private String 		replyAddress;

	@DBRef
	private User		userCreated;

	@DBRef
	private User		userModified;

	@DBRef
	private User		userApprovalRequested;

	@DBRef
	private User		userApproved;

	@DBRef
	private User		userPublished;

	private DateTime	dateCreated;

	private DateTime	dateModified;

	private DateTime	dateApprovalRequested;
	
	private DateTime	dateApproved;

	private DateTime	datePublished;

	private boolean		approvalRequested;
	
	private boolean		approved;

	private boolean		published;
	
	@DBRef
	private List<SubscriptionList>		subscriptionLists;

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

	public User getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(User userCreated) {
		this.userCreated = userCreated;
	}

	public User getUserModified() {
		return userModified;
	}

	public void setUserModified(User userModified) {
		this.userModified = userModified;
	}

	public User getUserApprovalRequested() {
		return userApprovalRequested;
	}

	public void setUserApprovalRequested(User userApprovalRequested) {
		this.userApprovalRequested = userApprovalRequested;
	}

	public User getUserApproved() {
		return userApproved;
	}

	public void setUserApproved(User userApproved) {
		this.userApproved = userApproved;
	}

	public User getUserPublished() {
		return userPublished;
	}

	public void setUserPublished(User userPublished) {
		this.userPublished = userPublished;
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

	public List<SubscriptionList> getSubscriptionLists() {
		return subscriptionLists;
	}

	public void setSubscriptionLists(List<SubscriptionList> subscriptionLists) {
		this.subscriptionLists = subscriptionLists;
	}
}
