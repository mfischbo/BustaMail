package de.mfischbo.bustamail.ftp.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.ftplet.FtpFile;

import de.mfischbo.bustamail.ftp.BustaMailFileSystemView;
import de.mfischbo.bustamail.security.domain.OrgUnit;

public class OrgUnitDirectory implements BustaFtpFile {

	private OrgUnit unit;
	private BustaFtpFile	parent;
	private BustaMailFileSystemView fsView;
	
	public OrgUnitDirectory(OrgUnit unit, BustaFtpFile parent, BustaMailFileSystemView fsView) {
		this.unit = unit;
		this.parent = parent;
		this.fsView = fsView;
	}
	
	public OrgUnit getOrgUnit() {
		return this.unit;
	}
	
	@Override
	public InputStream createInputStream(long arg0) throws IOException {
		return null;
	}

	@Override
	public OutputStream createOutputStream(long arg0) throws IOException {
		return null;
	}

	@Override
	public boolean delete() {
		return false;
	}

	@Override
	public boolean doesExist() {
		return true;
	}

	@Override
	public String getAbsolutePath() {
		return "/" + this.unit.getName();
	}

	@Override
	public String getGroupName() {
		return "bustamail";
	}

	@Override
	public long getLastModified() {
		return this.unit.getDateModified().getMillis();
	}

	@Override
	public int getLinkCount() {
		return 0;
	}

	@Override
	public String getName() {
		return this.unit.getName();
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
		List<FtpFile> retval = new ArrayList<>(2);
		retval.add(new AliasDirectory("templates", this, this.fsView));
		retval.add(new AliasDirectory("media", this, this.fsView));
		return retval;
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
	public BustaFtpFile getParent() {
		return this.parent;
	}

	@Override
	public String toString() {
		return "OrgUnitFtpDirectory [getAbsolutePath()=" + getAbsolutePath()
				+ ", getName()=" + getName() + "]";
	}
}
