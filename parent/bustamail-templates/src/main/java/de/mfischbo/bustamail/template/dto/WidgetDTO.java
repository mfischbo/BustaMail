package de.mfischbo.bustamail.template.dto;

import de.mfischbo.bustamail.common.dto.BaseDTO;

public class WidgetDTO extends BaseDTO {
	
	private static final long serialVersionUID = 2064658177343798737L;
	private String			name;
	private String			description;
	private String			source;
	
	
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
	
}
