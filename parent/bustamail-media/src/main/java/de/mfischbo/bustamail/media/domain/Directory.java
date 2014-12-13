package de.mfischbo.bustamail.media.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;

@Document(collection = "Media_Directory")
public class Directory extends OwnedBaseDomain {

	private static final long serialVersionUID = -1066888517355528723L;

	@Indexed
	private String			name;
	
	private String			description;

	private List<Directory> children = new ArrayList<>();
	
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
	public List<Directory> getChildren() {
		return children;
	}
	public void setChildren(List<Directory> children) {
		this.children = children;
	}
}
