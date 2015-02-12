package de.mfischbo.bustamail.ftp.domain;

import org.apache.ftpserver.ftplet.FtpFile;

public interface BustaFtpFile extends FtpFile {

	public BustaFtpFile getParent();
}
