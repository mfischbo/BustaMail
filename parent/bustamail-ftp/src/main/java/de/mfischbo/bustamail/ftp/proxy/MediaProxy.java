package de.mfischbo.bustamail.ftp.proxy;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mfischbo.bustamail.ftp.domain.BaseFtpFile;
import de.mfischbo.bustamail.media.domain.Directory;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;

public class MediaProxy implements ISourceProxy {

	private MediaService 		service;
	private Logger		 		log = LoggerFactory.getLogger(getClass());
	private ByteArrayOutputStream outStream;
	
	public MediaProxy(MediaService m) {
		this.service = m;
	}
	
	@Override
	public InputStream getInputStream(BaseFtpFile file) {
		try {
			Media m = service.getMediaById(file.getId());
			return service.getContent(m);
		} catch (Exception ex) {
			log.warn("Unable to get media for file [{}] : {}", file.getId(), file.getName());
		}
		return null;
	}

	@Override
	public OutputStream getOutputStream(BaseFtpFile file) {
		outStream = new ByteArrayOutputStream();
		return outStream;
	}

	@Override
	public boolean persist(BaseFtpFile file) {
		try {
			Directory d = service.getDirectoryById(file.getParent().getId());
			Media m = new Media();
			m.setName(file.getName());
			m.setData(this.outStream);
			m.setOwner(d.getOwner());
			service.createMedia(m, d);
			file.setPersistent(true);
			this.outStream = null;
			return true;
		} catch (Exception ex) {
			log.error("Unable to create media for filename {}.", file.getName());
		}
		return false;
	}
}
