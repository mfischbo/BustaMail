package de.mfischbo.bustamail.template.dto;

import java.util.List;

import de.mfischbo.bustamail.common.dto.OwnedBaseDTO;
import de.mfischbo.bustamail.media.dto.MediaImageDTO;

public class TemplatePackIndexDTO extends OwnedBaseDTO {

	private static final long serialVersionUID = -4824633333737272484L;
	private String name;
	private String description;
	private List<TemplateIndexDTO> templates;
	private MediaImageDTO			themeImage;
	

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
	public List<TemplateIndexDTO> getTemplates() {
		return templates;
	}
	public void setTemplates(List<TemplateIndexDTO> templates) {
		this.templates = templates;
	}
	public MediaImageDTO getThemeImage() {
		return themeImage;
	}
	public void setThemeImage(MediaImageDTO themeImage) {
		this.themeImage = themeImage;
	}
}
