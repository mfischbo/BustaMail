package de.mfischbo.bustamail.mailing.domain;

import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.security.domain.User;

@Entity
@Table(name = "Mailing_VersionedContent")
public class VersionedContent extends BaseDomain {

	private static final long serialVersionUID = 2960183880457152474L;

	public enum ContentType {
		HTML,
		Text
	}
	
	@Basic
	private DateTime		dateCreated;
	
	@ManyToOne
	@JoinColumn(name = "User_Created_id", referencedColumnName = "id")
	private User			userCreated;

	@Basic(fetch = FetchType.EAGER)
	@Column(length = 16)
	private UUID			mailingId;
	
	@Lob
	@Fetch(FetchMode.SELECT)
	private String			content;

	@Basic
	@Enumerated(EnumType.STRING)
	private ContentType		type;

	
	public DateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public User getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(User userCreated) {
		this.userCreated = userCreated;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UUID getMailingId() {
		return mailingId;
	}

	public void setMailingId(UUID mailingId) {
		this.mailingId = mailingId;
	}

	public ContentType getType() {
		return type;
	}

	public void setType(ContentType type) {
		this.type = type;
	}
}
