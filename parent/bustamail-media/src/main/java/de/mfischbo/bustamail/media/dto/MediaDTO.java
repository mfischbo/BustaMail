package de.mfischbo.bustamail.media.dto;

import de.mfischbo.bustamail.common.dto.OwnedBaseDTO;

public class MediaDTO extends OwnedBaseDTO {

	private static final long serialVersionUID = -63778087023536062L;

	private String		name;
	private String		description;
	private String		mimetype;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimeType) {
		this.mimetype = mimeType;
	}
}
