package de.mfischbo.bustamail.media.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;

public class Media extends OwnedBaseDomain {

	private static final long serialVersionUID = 2299514868852775624L;

	@Indexed
	private String name;
	
	private String description;
	
	private String mimetype;
	
	private Integer	height;
	
	private Integer width;
	
	private int     colorspace;
	
	private ObjectId	parent;
	
	private	ObjectId	directory;

	@Transient
	@JsonIgnore
	private byte[] 	data;

	
	public Media() {
		
	}
	
	public Media(ObjectId id, String filename, DBObject md) {
		this.id 		 = id;
		this.name		 = filename;
		this.description = (String) md.get(Media.KEY_DESCRIPTION);
		this.mimetype    = (String) md.get(Media.KEY_MIMETYPE);
		this.owner		 = (ObjectId) md.get(Media.KEY_OWNER);
		this.parent		 = (ObjectId) md.get(Media.KEY_PARENT);
		
		// image related data
		if (this.mimetype.startsWith("image")) {
			this.width 	= (Integer) md.get(Media.KEY_WIDTH);
			this.height = (Integer) md.get(Media.KEY_HEIGHT);
			this.colorspace = (int) md.get(Media.KEY_COLORSPACE);
		}
	}
	
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


	public ObjectId getDirectory() {
		return directory;
	}

	public void setDirectory(ObjectId directory) {
		this.directory = directory;
	}

	public String getExtension() {
		if (this.name != null && this.name.contains(".")) {
			return this.name.substring(this.name.lastIndexOf(".") +1);
		}
		return "";
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}
	
	public int getColorspace() {
		return colorspace;
	}

	public void setColorspace(int colorspace) {
		this.colorspace = colorspace;
	}

	public ObjectId getParent() {
		return parent;
	}

	public void setParent(ObjectId parent) {
		this.parent = parent;
	}

	@Transient
	@JsonIgnore
	public byte[] getData() {
		return this.data;
	}
	
	@Transient
	public void setData(byte[] data) {
		this.data = data;
	}
	
	@Transient
	public void setData(InputStream stream) {
		try {
			this.data = IOUtils.toByteArray(stream);
		} catch (Exception ex) {
			
		}
	}
	
	@Transient
	public InputStream asStream() {
		return new ByteArrayInputStream(this.data);
	}

	@Transient
	@JsonIgnore
	public DBObject getMetaData() {
		DBObject r = new BasicDBObject(); 
		r.put(KEY_DESCRIPTION, this.description);
		r.put(KEY_MIMETYPE, this.mimetype);
		r.put(KEY_DIRECTORY, this.directory);
		r.put(KEY_WIDTH, this.width);
		r.put(KEY_HEIGHT, this.height);
		r.put(KEY_EXTENSION, this.getExtension());
		r.put(KEY_COLORSPACE, this.colorspace);
		r.put(KEY_OWNER, this.getOwner());
		r.put(KEY_PARENT, this.parent);
		return r;
	}
	
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_MIMETYPE    = "mimetype";
	public static final String KEY_DIRECTORY   = "directory";
	public static final String KEY_WIDTH       = "width";
	public static final String KEY_HEIGHT		= "height";
	public static final String KEY_EXTENSION 	= "extension";
	public static final String KEY_COLORSPACE	= "colorspace";
	public static final String KEY_OWNER		= "owner";
	public static final String KEY_PARENT		= "parent";
}
