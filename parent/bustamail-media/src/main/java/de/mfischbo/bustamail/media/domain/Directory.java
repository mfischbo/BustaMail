package de.mfischbo.bustamail.media.domain;

import java.util.List;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;

public class Directory extends OwnedBaseDomain {

	private static final long serialVersionUID = -1066888517355528723L;

	private String			name;
	
	private String			description;

	private Directory		parent;

	private List<Directory> children;
	
	private List<Media>		files;
	
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
	public Directory getParent() {
		return parent;
	}
	public void setParent(Directory parent) {
		this.parent = parent;
	}
	public List<Directory> getChildren() {
		return children;
	}
	public void setChildren(List<Directory> children) {
		this.children = children;
	}
	public List<Media> getFiles() {
		return files;
	}
	public void setFiles(List<Media> files) {
		this.files = files;
	}
}
