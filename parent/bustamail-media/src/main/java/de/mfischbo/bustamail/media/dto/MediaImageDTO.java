package de.mfischbo.bustamail.media.dto;

import java.util.List;

public class MediaImageDTO extends MediaDTO {
	private static final long serialVersionUID = 8853537171085907540L;
	
	private int width;
	private int height;
	private int awtColorSpace;
	private List<MediaImageDTO>		variants;
	
	
	
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
	public List<MediaImageDTO> getVariants() {
		return variants;
	}
	public void setVariants(List<MediaImageDTO> variants) {
		this.variants = variants;
	}
}
