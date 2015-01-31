package de.mfischbo.bustamail.template.domain;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonView;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;
import de.mfischbo.bustamail.media.domain.Media;

@Document(collection = "Templates_TemplatePack")
public class TemplatePack extends OwnedBaseDomain {

	private static final long serialVersionUID = 8606991297872941503L;

	@JsonView(Object.class)
	private String			name;

	@JsonView(Object.class)
	private String			description;

	private Media			themeImage;

	private List<Template>	templates = new LinkedList<>();
	
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

	public Media getThemeImage() {
		return themeImage;
	}

	public void setThemeImage(Media themeImage) {
		this.themeImage = themeImage;
	}
}