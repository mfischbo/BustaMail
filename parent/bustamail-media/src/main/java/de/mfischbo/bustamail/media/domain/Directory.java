package de.mfischbo.bustamail.media.domain;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;

@Entity
@Table(name = "Media_Directory")
public class Directory extends OwnedBaseDomain {

	@Basic
	private String			name;
	
	@Basic
	@Column(length = 4095)
	private String			description;

	@ManyToOne
	@JoinColumn(name = "Parent_id", referencedColumnName = "id", nullable = true)
	private Directory		parent;

	@OneToMany(mappedBy = "parent")
	private List<Directory> children;
	
	@OneToMany(mappedBy = "directory", orphanRemoval = false)
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
