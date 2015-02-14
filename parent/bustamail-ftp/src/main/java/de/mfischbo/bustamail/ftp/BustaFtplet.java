package de.mfischbo.bustamail.ftp;

import javax.inject.Inject;

import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import de.mfischbo.bustamail.ftp.domain.BaseFtpDirectory;
import de.mfischbo.bustamail.ftp.domain.BaseFtpFile;
import de.mfischbo.bustamail.ftp.proxy.MediaProxy;
import de.mfischbo.bustamail.ftp.proxy.TemplatePackProxy;
import de.mfischbo.bustamail.media.service.MediaService;
import de.mfischbo.bustamail.template.domain.TemplatePack;
import de.mfischbo.bustamail.template.service.TemplateService;

@Component
public class BustaFtplet extends DefaultFtplet {

	@Inject private MediaService		mService;

	@Inject private TemplateService		tService;

	/*
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
	*/
	
	@Override
	public FtpletResult onMkdirEnd(FtpSession session, FtpRequest request) {
		BustaFSView view = (BustaFSView) session.getFileSystemView();
		return FtpletResult.DEFAULT;
	}

	@Override
	public FtpletResult onUploadStart(FtpSession session, FtpRequest request) {
		BustaFSView view = (BustaFSView) session.getFileSystemView();
		try {
			BaseFtpDirectory cwd = (BaseFtpDirectory) view.getWorkingDirectory();
		
			// decide whether we have a media or template file to upload
			String[] parts = cwd.getAbsolutePath().split("/");
			if (parts.length <= 2)
				return FtpletResult.SKIP;
		
			// media files always use the media proxy
			if (parts[2].equals("media")) {
				BaseFtpFile nf = new BaseFtpFile(request.getArgument(), cwd, cwd.getOwner(), new MediaProxy(mService));
				nf.setDateCreated(DateTime.now());
				nf.setDateModified(DateTime.now());
				nf.setPersistent(false);
				nf.setSize(0);
				cwd.getSubFiles().add(nf);
				return FtpletResult.DEFAULT;
			}
		
			// handle template files
			if (parts[2].equals("templates")) {
				
				// allowed paths for uploading are:
				// parts[4] -> The template directory (files: index.html, head.html)
				// parts[5] -> The image direcory -> MediaProxy
				// parts[5] -> The files directory -> MediaProxy
				// parts[5] -> the widgets directory -> Template proxy
				if (parts.length < 5) return FtpletResult.SKIP;
				TemplatePack pack = selectTemplatePack(cwd);
				if (pack == null)
					return FtpletResult.SKIP;
				
				// if we are in the Template directory
				if (parts.length == 5) {
					// allow uploads for index.html and head.html
					if (request.getArgument().equals("index.html") || request.getArgument().equals("head.html")) {
						return FtpletResult.DEFAULT;
					}
				}
				
				// all other files in subdirectories
				if (parts.length == 6) {
					BaseFtpFile file = (BaseFtpFile) view.getFile(request.getArgument());
					if (file != null) {
						return FtpletResult.DEFAULT;
					} else {
						file = new BaseFtpFile(request.getArgument(), cwd, cwd.getOwner(), new TemplatePackProxy(tService, pack, mService));
						file.setDateCreated(DateTime.now());
						file.setDateModified(DateTime.now());
						file.setPersistent(false);
						file.setSize(0L);
						cwd.getSubFiles().add(file);
						return FtpletResult.DEFAULT;
					}
				}
			}
			
		} catch (FtpException ex) {
			return FtpletResult.SKIP;
		}
		return FtpletResult.SKIP;
	}
	
	private TemplatePack selectTemplatePack(BaseFtpDirectory cwd) {
		// get the id of the directory that represents the tempate pack
		BaseFtpDirectory dir = cwd;
		while (!dir.getParent().getName().equals("templates")) {
			dir = dir.getParent();
		}
		try {
			TemplatePack retval = tService.getTemplatePackById(dir.getId());
			return retval;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) {
		BustaFSView view = (BustaFSView) session.getFileSystemView();
		try {
			BaseFtpFile file = (BaseFtpFile) view.getFile(request.getArgument());
			boolean success = file.persist();
			if (!success)
				return FtpletResult.SKIP;
		} catch (Exception ex) {
			
		}
		return FtpletResult.DEFAULT;
	}

}
