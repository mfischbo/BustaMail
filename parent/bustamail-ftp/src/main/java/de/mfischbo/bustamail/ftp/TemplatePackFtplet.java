package de.mfischbo.bustamail.ftp;

import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;

public class TemplatePackFtplet extends DefaultFtplet {

	@Override
	public FtpletResult onConnect(FtpSession session) {
		return FtpletResult.DEFAULT;
	}

	@Override
	public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) {
		FileSystemView view = session.getFileSystemView();
		if (view instanceof BustaMailFileSystemView) {
			BustaMailFileSystemView fsView = (BustaMailFileSystemView) view;
			fsView.persistPendingMedia();
		}
		return FtpletResult.DEFAULT;
	}

}
