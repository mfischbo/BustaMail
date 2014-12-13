package de.mfischbo.bustamail.template.domain;

import de.mfischbo.bustamail.common.domain.BaseDomain;

public class Widget extends BaseDomain {

	private static final long serialVersionUID = 7619850045491333396L;

	private String		name;
	
	private String		description;
	
	private String		source;
	
	private Template	template;

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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}
}
