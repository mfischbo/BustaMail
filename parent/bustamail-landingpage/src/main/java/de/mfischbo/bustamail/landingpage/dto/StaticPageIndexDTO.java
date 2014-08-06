package de.mfischbo.bustamail.landingpage.dto;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.template.dto.TemplateIndexDTO;

public class StaticPageIndexDTO extends BaseDomain {

	private static final long serialVersionUID = 2727341025712066827L;
	private String name;
	private String description;
	private LandingPageIndexDTO		parent;
	private TemplateIndexDTO		template;
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
	public LandingPageIndexDTO getParent() {
		return parent;
	}
	public void setParent(LandingPageIndexDTO parent) {
		this.parent = parent;
	}
	public TemplateIndexDTO getTemplate() {
		return template;
	}
	public void setTemplate(TemplateIndexDTO template) {
		this.template = template;
	}
}
