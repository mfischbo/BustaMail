package de.mfischbo.bustamail.ftp.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.ftpserver.ftplet.FtpFile;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import de.mfischbo.bustamail.security.domain.OrgUnit;

public class BaseFtpFile implements FtpFile {
	
	private ObjectId		id;
	private String name;
	private BaseFtpDirectory parent;
	private OrgUnit owner;
	private DateTime	dateCreated;
	private DateTime	dateModified;
	private boolean		_persistent;
	private long		size;
	
	
	public BaseFtpFile(String name, BaseFtpDirectory parent, OrgUnit owner) {
		this.name = name;
		this.parent = parent;
		this.owner = owner;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public void setDateCreated(DateTime date) {
		this.dateCreated = date;
	}
	
	public void setDateModified(DateTime date) {
		this.dateModified = date;
	}
	
	public void setPersistent(boolean persistent) {
		this._persistent = persistent;
	}
	
	public boolean isPersistent(boolean persistent) {
		return this._persistent;
	}
	
	public void setSize(long size) {
		this.size = size;
	}
	
	
	@Override
	public InputStream createInputStream(long arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream createOutputStream(long arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doesExist() {
		return this._persistent;
	}

	@Override
	public String getAbsolutePath() {
		return this.parent.getAbsolutePath() + "/" + this.name;
	}

	@Override
	public String getGroupName() {
		return this.owner.getName();
	}

	@Override
	public long getLastModified() {
		if (this.dateModified == null) return 0;
		return this.dateModified.getMillis();
	}

	@Override
	public int getLinkCount() {
		return 0;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getOwnerName() {
		return this.owner.getName();
	}

	@Override
	public long getSize() {
		return this.size;
	}

	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public boolean isFile() {
		return true;
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public boolean isReadable() {
		return true;
	}

	@Override
	public boolean isRemovable() {
		return true;
	}

	@Override
	public boolean isWritable() {
		return true;
	}

	@Override
	public List<FtpFile> listFiles() {
		return null;
	}

	@Override
	public boolean mkdir() {
		return false;
	}

	@Override
	public boolean move(FtpFile arg0) {
		return false;
	}

	@Override
	public boolean setLastModified(long arg0) {
		return false;
	}

	@Override
	public String toString() {
		return "BaseFtpFile [getAbsolutePath()=" + getAbsolutePath() + "]";
	}
}
