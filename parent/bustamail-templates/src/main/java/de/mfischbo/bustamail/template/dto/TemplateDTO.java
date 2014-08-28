package de.mfischbo.bustamail.template.dto;

import java.util.HashMap;
import java.util.List;

import de.mfischbo.bustamail.media.dto.MediaDTO;
import de.mfischbo.bustamail.media.dto.MediaImageDTO;

public class TemplateDTO extends TemplateIndexDTO {

	private static final long serialVersionUID = -8509651144109797425L;

	private String 						source;
	private HashMap<String, String>		settings = new HashMap<>();
	private List<WidgetDTO>				widgets;
	private List<MediaImageDTO> 		images;
	private List<MediaDTO>				resources;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<WidgetDTO> getWidgets() {
		return widgets;
	}

	public void setWidgets(List<WidgetDTO> widgets) {
		this.widgets = widgets;
	}

	public List<MediaImageDTO> getImages() {
		return images;
	}

	public void setImages(List<MediaImageDTO> images) {
		this.images = images;
	}

	public List<MediaDTO> getResources() {
		return resources;
	}

	public void setResources(List<MediaDTO> resources) {
		this.resources = resources;
	}

	public HashMap<String, String> getSettings() {
		return settings;
	}

	public void setSettings(HashMap<String, String> settings) {
		this.settings = settings;
	}
}
