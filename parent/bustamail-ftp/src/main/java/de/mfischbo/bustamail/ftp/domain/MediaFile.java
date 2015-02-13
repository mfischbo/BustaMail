package de.mfischbo.bustamail.ftp.domain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.ftpserver.ftplet.FtpFile;

import de.mfischbo.bustamail.ftp.BustaMailFileSystemView;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;

public class MediaFile implements FtpFile {

	private Media media;
	private BustaFtpFile	parent;
	private MediaService service;
	
	public MediaFile(Media m, BustaFtpFile parent, BustaMailFileSystemView fsView) {
		this.media = m;
		this.parent = parent;
		this.service = fsView.getMediaService();
	}
	
	@Override
	public InputStream createInputStream(long arg0) throws IOException {
		try {
			return service.getContent(media);
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public OutputStream createOutputStream(long arg0) throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		media.setData(outStream);
		return outStream;
	}

	@Override
	public boolean delete() {
		try {
			this.service.deleteMedia(this.media);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public boolean doesExist() {
		try {
			this.service.getMediaById(media.getId());
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public String getAbsolutePath() {
		return this.parent.getAbsolutePath() + "/" + this.media.getName();
	}

	@Override
	public String getGroupName() {
		return "bustamail";
	}

	@Override
	public long getLastModified() {
		if (media.getDateModified() != null)
			return media.getDateModified().getMillis();
		return 0;
	}

	@Override
	public int getLinkCount() {
		return 0;
	}

	@Override
	public String getName() {
		return this.media.getName();
	}

	@Override
	public String getOwnerName() {
		return "bustamail";
	}

	@Override
	public long getSize() {
		return media.getSize();
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
		return "MediaFtpFile [getAbsolutePath()=" + getAbsolutePath()
				+ ", getName()=" + getName() + "]";
	}
}
