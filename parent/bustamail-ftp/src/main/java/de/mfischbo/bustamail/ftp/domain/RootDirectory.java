package de.mfischbo.bustamail.ftp.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.ftpserver.ftplet.FtpFile;
import org.bson.types.ObjectId;

import de.mfischbo.bustamail.ftp.BustaFSView;
import de.mfischbo.bustamail.security.domain.OrgUnit;

public class RootDirectory implements BustaFtpFile {

	private Set<OrgUnit> units;
	
	private BustaFSView 	fsView;
	
	public RootDirectory(Set<OrgUnit> units, BustaFSView fsView) {
		this.units = units;
		this.fsView = fsView;
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
		return "/";
	}

	@Override
	public String getGroupName() {
		return "bustamail";
	}

	@Override
	public long getLastModified() {
		return 0;
	}

	@Override
	public int getLinkCount() {
		return 0;
	}

	@Override
	public String getName() {
		return "/";
	}

	@Override
	public String getOwnerName() {
		return "root";
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
		List<FtpFile> dirs = new ArrayList<>(this.units.size());
		this.units.forEach(u -> {
			dirs.add(new OrgUnitDirectory(u, this, this.fsView));
		});
		return dirs;
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
		return null;
	}

	@Override
	public String toString() {
		return "RootFtpFile [getAbsolutePath()=" + getAbsolutePath()
				+ ", getName()=" + getName() + "]";
	}

	@Override
	public ObjectId getId() {
		return null;
	}
}
