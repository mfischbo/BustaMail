package de.mfischbo.bustamail.media.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;

@Entity
@Table(name = "Media_Media")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Media extends OwnedBaseDomain {

	private static final long serialVersionUID = 2299514868852775624L;

	@Basic
	private String name;
	
	@Basic
	@Column(length = 4095)
	private String description;
	
	@Basic
	private String mimetype;
	
	@Lob
	@Fetch(FetchMode.SELECT)
	private byte[] data;
	
	@ManyToOne
	@JoinColumn(name = "Directory_id", referencedColumnName = "id", nullable = true)
	private	Directory	directory;

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

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Directory getDirectory() {
		return directory;
	}

	public void setDirectory(Directory directory) {
		this.directory = directory;
	}
	
	@Transient
	public InputStream getDataStream() {
		return new ByteArrayInputStream(this.data);
	}
	
	@Transient
	public String getExtension() {
		if (this.name != null && this.name.contains(".")) {
			return this.name.substring(this.name.lastIndexOf(".") +1);
		}
		return null;
	}
}
