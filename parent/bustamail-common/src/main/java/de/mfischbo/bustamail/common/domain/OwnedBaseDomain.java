package de.mfischbo.bustamail.common.domain;

import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class OwnedBaseDomain extends BaseDomain {

	private static final long serialVersionUID = -4608480581507322585L;
	
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "Owner_Id", length = 16, nullable = false)
	private UUID		owner;

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}
}
