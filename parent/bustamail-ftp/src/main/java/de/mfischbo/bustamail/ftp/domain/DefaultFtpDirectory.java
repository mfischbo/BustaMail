package de.mfischbo.bustamail.ftp.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.ftpserver.ftplet.FtpFile;

public class DefaultFtpDirectory implements BustaFtpFile {

	private String name;
	private BustaFtpFile parent;
	
	public DefaultFtpDirectory(String name, BustaFtpFile parent) {
		this.name = name;
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
		return parent.getAbsolutePath() + "/" + this.name;
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
		// TODO Auto-generated method stub
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
	public BustaFtpFile getParent() {
		return this.parent;
	}
}
