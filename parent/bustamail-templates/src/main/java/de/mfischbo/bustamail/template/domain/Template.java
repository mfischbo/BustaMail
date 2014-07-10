package de.mfischbo.bustamail.template.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.media.domain.MediaImage;

@Entity
@Table(name = "Templates_Template")
public class Template extends BaseDomain {

	private static final long serialVersionUID = -257611755576891163L;

	@Basic
	private String		name;
	
	@Basic
	@Column(length = 4095)
	private String		description;
	
	@Lob
	private String		source;

	@ManyToOne(optional = false)
	@JoinColumn(name = "TemplatePack_id", referencedColumnName = "id", nullable = false)
	private TemplatePack	templatePack;

	@OneToMany(mappedBy = "template", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Widget>	widgets;


	@ManyToMany
	@JoinTable(name = "Templates_TemplateImages",
		joinColumns 		= { @JoinColumn(name = "Template_id", referencedColumnName = "id")},
		inverseJoinColumns 	= { @JoinColumn(name = "Media_id",    referencedColumnName = "id")}
	)
	private List<MediaImage>	images;

	
	@CollectionTable(name = "Templates_TemplateSettings")
	@ElementCollection(fetch = FetchType.EAGER)
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
}
