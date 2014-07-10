package de.mfischbo.bustamail.template.domain;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;
import de.mfischbo.bustamail.media.domain.MediaImage;

@Entity
@Table(name = "Templates_TemplatePack")
public class TemplatePack extends OwnedBaseDomain {

	private static final long serialVersionUID = 8606991297872941503L;

	@Basic
	private String		name;
	
	@Basic
	@Column(length = 4095)
	private String		description;

	@OneToMany(mappedBy = "templatePack", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Template>		templates;
	
	@ManyToOne
	@JoinColumn(name = "MediaImage_id", referencedColumnName = "id", nullable = true)
	private MediaImage		themeImage;

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

	public MediaImage getThemeImage() {
		return themeImage;
	}

	public void setThemeImage(MediaImage themeImage) {
		this.themeImage = themeImage;
	}
}
