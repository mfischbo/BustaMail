package de.mfischbo.bustamail.ftp.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.ftpserver.ftplet.FtpFile;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import de.mfischbo.bustamail.security.domain.OrgUnit;

public class BaseFtpDirectory implements FtpFile {

	private ObjectId				id;
	private String 					name;
	private List<BaseFtpDirectory> 	subdirs = new LinkedList<>();
	private List<BaseFtpFile>		subfiles= new LinkedList<>();
	private BaseFtpDirectory 		parent;
	private OrgUnit					owner;
	private DateTime				dateModified;
	private boolean					_initialized;
	private boolean					_persistent;

	public BaseFtpDirectory(String name, BaseFtpDirectory parent, OrgUnit owner) {
		this.name 	= name;
		this.parent = parent;
		this.owner  = owner;
	}
	
	public List<BaseFtpDirectory> getSubdirs() {
		return this.subdirs;
	}
	
	public List<BaseFtpFile>	getSubFiles() {
		return this.subfiles;
	}
	
	public BaseFtpDirectory getParent() {
		return this.parent;
	}
	
	public OrgUnit getOwner() {
		return this.owner;
	}
	
	public void setDateModified(DateTime date) {
		this.dateModified = date;
	}
	
	public void setInitialized(boolean initialized) {
		this._initialized = initialized;
	}
	
	public boolean isInitialized() {
		return this._initialized;
	}
	
	public void setPersistend(boolean persistent) {
		this._persistent = persistent;
	}
	
	public boolean isPersistent() {
		return this._persistent;
	}
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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
		if (this.parent == null) 
			return this.name; 
		if (this.parent.getAbsolutePath().equals("/"))
			return this.parent.getAbsolutePath() + this.name;
		return this.parent.getAbsolutePath() + "/" + this.getName();
	}

	@Override
	public String getGroupName() {
		if (this.owner == null)
			return "bustamail";
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
		return "bustamail";
	}

	@Override
	public long getSize() {
		return 0;
	}

	@Override
	public boolean isDirectory() {
		return true;
	}

	@Override
	public boolean isFile() {
		return false;
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
		return false;
	}

	@Override
	public boolean isWritable() {
		return false;
	}

	@Override
	public List<FtpFile> listFiles() {
		List<FtpFile> retval = new ArrayList<>(this.subdirs.size() + this.subfiles.size());
		retval.addAll(this.subdirs);
		retval.addAll(this.subfiles);
		return retval;
	}

	@Override
	public boolean mkdir() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean move(FtpFile arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setLastModified(long arg0) {
		return true;
	}

	@Override
	public String toString() {
		return "BaseFtpDirectory [getAbsolutePath()=" + getAbsolutePath()
				+ ", getName()=" + getName() + "]";
	}
}
