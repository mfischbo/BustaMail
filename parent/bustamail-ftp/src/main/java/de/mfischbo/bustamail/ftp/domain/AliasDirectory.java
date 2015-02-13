package de.mfischbo.bustamail.ftp.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.ftplet.FtpFile;
import org.springframework.security.core.context.SecurityContextHolder;

import de.mfischbo.bustamail.ftp.BustaMailFileSystemView;
import de.mfischbo.bustamail.media.domain.Directory;
import de.mfischbo.bustamail.media.service.MediaService;

public class AliasDirectory implements BustaFtpFile {

	private String 			name;
	private BustaFtpFile 	parent;
	private BustaMailFileSystemView	fsView;
	
	public AliasDirectory(String name, BustaFtpFile parent, BustaMailFileSystemView fsView) {
		this.name = name;
		this.parent = parent;
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
		SecurityContextHolder.getContext().setAuthentication(fsView.getAuthentication());
		
		if (this.name.equals("media")) {
			MediaService mediaService = fsView.getMediaService();
			List<Directory> dirs = mediaService.getDirectoryRoots();
			List<FtpFile> retval = new ArrayList<FtpFile>(dirs.size());
			for (Directory d : dirs) {
				retval.add(new MediaDirectory(d, this, this.fsView));
			}
			return retval;
		}
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

	@Override
	public String toString() {
		return "DefaultFtpDirectory [getAbsolutePath()=" + getAbsolutePath()
				+ ", getName()=" + getName() + "]";
	}

	@Override
	public void addChild(BustaFtpFile file) {
	}
}
