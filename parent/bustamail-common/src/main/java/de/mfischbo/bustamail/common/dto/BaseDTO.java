package de.mfischbo.bustamail.common.dto;

import java.io.Serializable;

import org.bson.types.ObjectId;

public class BaseDTO implements Serializable {

	private static final long serialVersionUID = -2147385171071746838L;
	protected ObjectId id;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
}
