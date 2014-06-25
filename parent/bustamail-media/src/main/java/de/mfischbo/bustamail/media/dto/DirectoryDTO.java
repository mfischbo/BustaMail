package de.mfischbo.bustamail.media.dto;

import java.util.List;

import de.mfischbo.bustamail.common.dto.OwnedBaseDTO;

public class DirectoryDTO extends OwnedBaseDTO {

	private static final long serialVersionUID = -5031087107626485063L;
	private String name;
	private String description;
	
	private List<DirectoryDTO>  children;
	
	//private List<MediaDTO>		files;
	
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
	public List<DirectoryDTO> getChildren() {
		return children;
	}
	public void setChildren(List<DirectoryDTO> children) {
		this.children = children;
	}
	
	/*
	public List<MediaDTO> getFiles() {
		return files;
	}
	public void setFiles(List<MediaDTO> files) {
		this.files = files;
	}
	*/
}
