package de.mfischbo.bustamail.template.dto;

import java.util.List;

import de.mfischbo.bustamail.common.dto.OwnedBaseDTO;

public class TemplatePackDTO extends OwnedBaseDTO {

	private static final long serialVersionUID = -1133830997946444839L;

	private String				name;
	private String				description;
	
	private List<TemplateDTO>	templates;


	
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


	public List<TemplateDTO> getTemplates() {
		return templates;
	}


	public void setTemplates(List<TemplateDTO> templates) {
		this.templates = templates;
	}
}
