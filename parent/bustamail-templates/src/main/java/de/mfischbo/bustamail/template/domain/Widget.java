package de.mfischbo.bustamail.template.domain;

import org.bson.types.ObjectId;

public class Widget {

	private ObjectId	id;
	
	private String		name;
	
	private String		description;
	
	private String		source;

	public Widget() {
		this.id = new ObjectId();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

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
