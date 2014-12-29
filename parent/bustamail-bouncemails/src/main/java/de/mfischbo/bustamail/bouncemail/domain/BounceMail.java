package de.mfischbo.bustamail.bouncemail.domain;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mfischbo.bustamail.common.domain.BaseDomain;

@Document(collection = "Bounce_BounceMail")
public class BounceMail extends BaseDomain {

	private static final long serialVersionUID = 5174801081999462493L;

	private ObjectId		accountId;

	public ObjectId getAccountId() {
		return accountId;
	}

	public void setAccountId(ObjectId accountId) {
		this.accountId = accountId;
	}
}
