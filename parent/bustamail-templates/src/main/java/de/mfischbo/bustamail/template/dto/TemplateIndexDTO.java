package de.mfischbo.bustamail.template.dto;

import de.mfischbo.bustamail.common.dto.BaseDTO;

public class TemplateIndexDTO extends BaseDTO {

	private static final long serialVersionUID = 4715595282679300182L;

	
	private String name;
	private String description;

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
}
