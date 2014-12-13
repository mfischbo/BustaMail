package de.mfischbo.bustamail.media.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;

@Document(collection = "Media")
public class Media extends OwnedBaseDomain {

	private static final long serialVersionUID = 2299514868852775624L;

	@Indexed
	private String name;
	
	private String description;
	
	private String mimetype;
	
	@Transient private byte[] data;
	
	@DBRef
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
	
	public InputStream getDataStream() {
		return new ByteArrayInputStream(this.data);
	}
	
	public String getExtension() {
		if (this.name != null && this.name.contains(".")) {
			return this.name.substring(this.name.lastIndexOf(".") +1);
		}
		return null;
	}
}
