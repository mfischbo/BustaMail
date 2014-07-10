package de.mfischbo.bustamail.security.domain;

import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;

import de.mfischbo.bustamail.common.domain.BaseDomain;

@Entity
@Table(name = "Security_OrgUnit")
public class OrgUnit extends BaseDomain {

	private static final long serialVersionUID = 1410454016750659990L;

	@Basic
	@NotBlank
	private String			name;
	
	@Basic
	@Column(length = 4096)
	private	String			description;

	@Basic(fetch = FetchType.EAGER)
	@Column(name = "dateCreated", nullable = false)
	private DateTime	dateCreated;
	
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "dateModified", nullable = false)
	private DateTime 	dateModified;
	
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "deleted", nullable = false)
	private boolean		deleted;
	
	@ManyToOne
	@JoinColumn(name = "ParentGroup_id", referencedColumnName = "id")
	private OrgUnit		parent;
	
	@OneToMany(mappedBy = "parent")
	private Set<OrgUnit>	children;
	
	@OneToMany(mappedBy = "orgUnit")
	private List<Actor> 	actors;
	

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

	public DateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public DateTime getDateModified() {
		return dateModified;
	}

	public void setDateModified(DateTime dateModified) {
		this.dateModified = dateModified;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public OrgUnit getParent() {
		return parent;
	}

	public void setParent(OrgUnit parent) {
		this.parent = parent;
	}

	public Set<OrgUnit> getChildren() {
		return children;
	}

	public void setChildren(Set<OrgUnit> children) {
		this.children = children;
	}

	public List<Actor> getActors() {
		return actors;
	}

	public void setActors(List<Actor> actors) {
		this.actors = actors;
	}

	@Override
	public String toString() {
		return "OrgUnit [name=" + name + ", id=" + id + "]";
	}
}
