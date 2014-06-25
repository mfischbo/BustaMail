package de.mfischbo.bustamail.common.dto;

import java.io.Serializable;
import java.util.UUID;

public class BaseDTO implements Serializable {

	private static final long serialVersionUID = -2147385171071746838L;
	protected UUID id;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
}
