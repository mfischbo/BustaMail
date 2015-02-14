package de.mfischbo.bustamail.ftp;

import javax.inject.Inject;

import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.springframework.stereotype.Component;

import de.mfischbo.bustamail.ftp.domain.BustaFtpFile;
import de.mfischbo.bustamail.ftp.domain.MediaDirectory;
import de.mfischbo.bustamail.media.domain.Directory;
import de.mfischbo.bustamail.media.service.MediaService;

@Component
public class BustaFtplet extends DefaultFtplet {

	@Inject private MediaService		mService;


	@Override
	public FtpletResult onMkdirStart(FtpSession session, FtpRequest request) {
		BustaFSView view = (BustaFSView) session.getFileSystemView();
		try {
			BustaFtpFile cwd = (BustaFtpFile) view.getWorkingDirectory();
			String dirname = request.getArgument();
			Directory d = new Directory();
			d.setName(dirname);
			Directory parent = mService.getDirectoryById(cwd.getId());
			//d = mService.createDirectory(view.getCurrentOwner().getId(), parent, d);
			MediaDirectory md = new MediaDirectory(d, cwd);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return FtpletResult.DEFAULT;
	}
	
	@Override
	public FtpletResult onMkdirEnd(FtpSession session, FtpRequest request) {
		BustaFSView view = (BustaFSView) session.getFileSystemView();
		return FtpletResult.DEFAULT;
	}

	@Override
	public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) {
		FileSystemView view = session.getFileSystemView();
		if (view instanceof BustaFSView) {
			BustaFSView fsView = (BustaFSView) view;
			fsView.persistPendingMedia();
		}
		return FtpletResult.DEFAULT;
	}

}
