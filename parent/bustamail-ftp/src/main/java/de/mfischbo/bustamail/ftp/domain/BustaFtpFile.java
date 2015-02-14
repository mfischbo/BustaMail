package de.mfischbo.bustamail.ftp.domain;

import org.apache.ftpserver.ftplet.FtpFile;
import org.bson.types.ObjectId;

public interface BustaFtpFile extends FtpFile {

	public ObjectId		getId();
	public BustaFtpFile getParent();
}
