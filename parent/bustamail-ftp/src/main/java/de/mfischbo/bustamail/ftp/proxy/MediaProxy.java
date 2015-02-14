package de.mfischbo.bustamail.ftp.proxy;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mfischbo.bustamail.ftp.domain.BaseFtpFile;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;

public class MediaProxy implements ISourceProxy {

	private MediaService service;
	private Logger		 log = LoggerFactory.getLogger(getClass());
	
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
		// TODO Auto-generated method stub
		return null;
	}

}
