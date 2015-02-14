package de.mfischbo.bustamail.ftp.proxy;

import java.io.InputStream;
import java.io.OutputStream;

import de.mfischbo.bustamail.ftp.domain.BaseFtpFile;

public interface ISourceProxy {

	public InputStream getInputStream(BaseFtpFile file);
	
	public OutputStream getOutputStream(BaseFtpFile file);
}
