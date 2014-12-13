package de.mfischbo.bustamail.media.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;

public class MediaImage extends Media {

	private static final long serialVersionUID = -2954918522384485908L;

	private int		width;
	
	private int		height;
	
	private int		awtColorSpace;
	
	@DBRef
	private MediaImage				parent;
	
	@DBRef
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
