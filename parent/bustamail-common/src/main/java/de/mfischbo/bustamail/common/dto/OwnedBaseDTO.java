package de.mfischbo.bustamail.common.dto;

import java.io.Serializable;
import java.util.UUID;

public class OwnedBaseDTO extends BaseDTO implements Serializable {
	
	private static final long serialVersionUID = -7729841894241679118L;
	private UUID owner;

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}
	
	

}
