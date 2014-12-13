package de.mfischbo.bustamail.template.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.DBRef;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.domain.MediaImage;

public class Template extends BaseDomain {

	private static final long serialVersionUID = -257611755576891163L;
	
	public static final String SKEY_CELLPADDING_OPTIMIZATION = "enableCellPaddingOptimization";

	private String		name;
	
	private String		description;
	
	private String		htmlHead;
	
	private String		source;

	@DBRef
	private TemplatePack	templatePack;

	private List<Widget>	widgets;

	private List<MediaImage>	images;
	
	private List<Media>			resources;

	private Map<String, String>		settings = new LinkedHashMap<>();
	
	
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

	public TemplatePack getTemplatePack() {
		return templatePack;
	}

	public void setTemplatePack(TemplatePack templatePack) {
		this.templatePack = templatePack;
	}
	
	public List<Widget> getWidgets() {
		return widgets;
	}

	public void setWidgets(List<Widget> widgets) {
		this.widgets = widgets;
	}

	public List<MediaImage> getImages() {
		return images;
	}

	public void setImages(List<MediaImage> images) {
		this.images = images;
	}

	public List<Media> getResources() {
		return resources;
	}

	public void setResources(List<Media> resources) {
		this.resources = resources;
	}
}
