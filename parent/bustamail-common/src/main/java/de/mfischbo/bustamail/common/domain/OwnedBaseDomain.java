package de.mfischbo.bustamail.common.domain;

import org.bson.types.ObjectId;

public class OwnedBaseDomain extends BaseDomain {

	private static final long serialVersionUID = -4608480581507322585L;
	
	protected ObjectId		owner;

	public ObjectId getOwner() {
		return owner;
	}

	public void setOwner(ObjectId owner) {
		this.owner = owner;
	}
}
