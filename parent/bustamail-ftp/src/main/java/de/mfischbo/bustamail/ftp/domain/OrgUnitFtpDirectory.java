package de.mfischbo.bustamail.ftp.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.ftplet.FtpFile;

import de.mfischbo.bustamail.security.domain.OrgUnit;

public class OrgUnitFtpDirectory implements BustaFtpFile {

	private OrgUnit unit;
	
	private BustaFtpFile	parent;
	
	public OrgUnitFtpDirectory(OrgUnit unit, BustaFtpFile parent) {
		this.unit = unit;
		this.parent = parent;
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
		return "/" + this.unit.getName().replaceAll(" ", "_");
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
		return this.unit.getName().replaceAll(" ", "_");
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
		return false;
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
		retval.add(new DefaultFtpDirectory("templates", this));
		retval.add(new DefaultFtpDirectory("media", this));
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
}
