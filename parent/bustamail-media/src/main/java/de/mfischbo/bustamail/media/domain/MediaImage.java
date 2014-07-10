package de.mfischbo.bustamail.media.domain;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Media_MediaImage")
public class MediaImage extends Media {

	private static final long serialVersionUID = -2954918522384485908L;

	@Basic
	private int		width;
	
	@Basic
	private int		height;
	
	@Basic
	private int		awtColorSpace;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "Parent_id", referencedColumnName = "id")
	private MediaImage				parent;
	
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	private List<MediaImage>		variants;

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getAwtColorSpace() {
		return awtColorSpace;
	}

	public void setAwtColorSpace(int awtColorSpace) {
		this.awtColorSpace = awtColorSpace;
	}

	public MediaImage getParent() {
		return parent;
	}

	public void setParent(MediaImage parent) {
		this.parent = parent;
	}

	public List<MediaImage> getVariants() {
		return variants;
	}

	public void setVariants(List<MediaImage> variants) {
		this.variants = variants;
	}
}
