package de.mfischbo.bustamail.common.dto;

import java.io.Serializable;

import org.bson.types.ObjectId;

public class OwnedBaseDTO extends BaseDTO implements Serializable {
	
	private static final long serialVersionUID = -7729841894241679118L;
	private ObjectId owner;

	public ObjectId getOwner() {
		return owner;
	}

	public void setOwner(ObjectId owner) {
		this.owner = owner;
	}
	
	

}
