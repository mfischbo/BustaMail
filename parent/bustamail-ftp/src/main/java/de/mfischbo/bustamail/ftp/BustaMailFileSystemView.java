package de.mfischbo.bustamail.ftp;

import java.util.Set;

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import de.mfischbo.bustamail.ftp.domain.BustaFtpFile;
import de.mfischbo.bustamail.ftp.domain.MediaDirectory;
import de.mfischbo.bustamail.ftp.domain.MediaFile;
import de.mfischbo.bustamail.ftp.domain.RootDirectory;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.service.SecurityService;
import de.mfischbo.bustamail.template.service.TemplateService;

public class BustaMailFileSystemView implements FileSystemView {

	private Authentication		auth;
	
	private TemplateService 	tService;

	private SecurityService		secService;
	
	private MediaService		mediaService;
	
	private BustaFtpFile	    root;

	private BustaFtpFile		cwd;
	
	public BustaMailFileSystemView(TemplateService tService, SecurityService secService, MediaService mediaService, Authentication auth) {
		this.tService = tService;
		this.secService = secService;
		this.mediaService = mediaService;
		this.auth = auth;
		
		Set<OrgUnit> units = secService.getOrgUnitsByCurrentUser();
		this.root = new RootDirectory(units, this);
		this.cwd  = this.root;
	}
	
	private void navigateTo(String path) {
		if (path.equals("."))
			return;
		
		if (path.equals("..")) {
			if (this.cwd == this.root) return;
			this.cwd = this.cwd.getParent();
			return;
		}
		
		for (FtpFile c : this.cwd.listFiles()) {
			if (c.getName().equals(path)) {
				this.cwd = (BustaFtpFile) c;
				return;
			}
		}
	}
	
	@Override
	public boolean changeWorkingDirectory(String arg0) throws FtpException {
		SecurityContextHolder.getContext().setAuthentication(auth);
		String[] parts = arg0.split("/");
		for (int i=0; i < parts.length; i++) {
			navigateTo(parts[i]);
		}
		return true;
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public FtpFile getFile(String arg0) throws FtpException {
		if (arg0.equals("/") || arg0.equals("./")) {
			return this.cwd;
		} else {
			
			// check if file is present
			for (FtpFile f : this.cwd.listFiles()) {
				if (f.getName().equals(arg0))
					return f;
			}
			
			if (this.cwd instanceof MediaDirectory) {
				// create a new file
				Media m = new Media();
				m.setName(arg0);
				MediaFile f = new MediaFile(m, this.cwd, this);
				this.cwd.addChild((BustaFtpFile) f);
				return f;
			// well... this could become an issue
			}
		}
		return null;
	}

	@Override
	public FtpFile getHomeDirectory() throws FtpException {
		return this.root;
	}

	@Override
	public FtpFile getWorkingDirectory() throws FtpException {
		return this.cwd;
	}

	@Override
	public boolean isRandomAccessible() throws FtpException {
		return false;
	}
	
	public MediaService getMediaService() {
		return this.mediaService;
	}
	
	public TemplateService getTemplateService() {
		return this.tService;
	}
	
	public Authentication getAuthentication() {
		return this.auth;
	}
	
	public SecurityService getSecurityService() {
		return this.secService;
	}
}
