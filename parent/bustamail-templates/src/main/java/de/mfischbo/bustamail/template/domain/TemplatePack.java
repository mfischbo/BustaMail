package de.mfischbo.bustamail.template.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;
import de.mfischbo.bustamail.media.domain.MediaImage;

@Document(collection = "TemplatePack")
public class TemplatePack extends OwnedBaseDomain {

	private static final long serialVersionUID = 8606991297872941503L;

	private String			name;
	
	private String			description;

	private List<Template>	templates;
	
	private MediaImage		themeImage;
	
	
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

	public List<Template> getTemplates() {
		return templates;
	}

	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}

	public MediaImage getThemeImage() {
		return themeImage;
	}

	public void setThemeImage(MediaImage themeImage) {
		this.themeImage = themeImage;
	}
}
