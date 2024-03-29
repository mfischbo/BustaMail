package de.mfischbo.bustamail.template.domain;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import de.mfischbo.bustamail.media.domain.Media;

public class Template {
	
	public static final String SKEY_CELLPADDING_OPTIMIZATION = "enableCellPaddingOptimization";

	private ObjectId		id;
	
	private String			name;
	
	private String			description;
		
	private String			htmlHead;
	
	private String			source;

	private List<Widget>	widgets = new LinkedList<>();

	private List<Media>		images = new LinkedList<>();
	
	private List<Media>		resources = new LinkedList<>();

	private Map<String, String>		settings = new LinkedHashMap<>();
	
	private boolean			editable = true;

	public Template() {
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
	
	public Map<String, String> getSettings() {
		return settings;
	}

	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}
	
	public String getHtmlHead() {
		return htmlHead;
	}

	public void setHtmlHead(String htmlHead) {
		this.htmlHead = htmlHead;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<Widget> getWidgets() {
		return widgets;
	}

	public void setWidgets(List<Widget> widgets) {
		this.widgets = widgets;
	}

	public List<Media> getImages() {
		return images;
	}

	public void setImages(List<Media> images) {
		this.images = images;
	}

	public List<Media> getResources() {
		return resources;
	}

	public void setResources(List<Media> resources) {
		this.resources = resources;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
}
