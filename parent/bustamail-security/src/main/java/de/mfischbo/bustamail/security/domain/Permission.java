package de.mfischbo.bustamail.security.domain;

import java.util.UUID;

public class Permission {

	private	UUID		id;
	private String		group;
	private String		identificator;		// use for springs @PreAuthorize
	private String		nicename;
	private String		description;
	
	public Permission(UUID id, String group, String identificator, String nicename, String description) {
		this.id = id;
		this.group = group;
		this.identificator = identificator;
		this.nicename = nicename;
		this.description = description;
	}

	public UUID getId() {
		return id;
	}

	public String getGroup() {
		return group;
	}

	public String getIdentificator() {
		return identificator;
	}

	public String getNicename() {
		return nicename;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Permission other = (Permission) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
