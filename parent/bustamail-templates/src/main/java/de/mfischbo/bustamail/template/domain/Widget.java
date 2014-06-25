package de.mfischbo.bustamail.template.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.mfischbo.bustamail.common.domain.BaseDomain;

@Entity
@Table(name = "Templates_Widget")
public class Widget extends BaseDomain {

	@Basic
	private String		name;
	
	@Basic
	@Column(length = 4095)
	private String		description;
	
	@Lob
	private String		source;
	
	@ManyToOne
	@JoinColumn(name = "Template_id", referencedColumnName = "id", nullable = true)
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
