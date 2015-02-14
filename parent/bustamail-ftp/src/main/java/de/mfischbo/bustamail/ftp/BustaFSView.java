package de.mfischbo.bustamail.ftp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import de.mfischbo.bustamail.ftp.domain.BaseFtpDirectory;
import de.mfischbo.bustamail.ftp.domain.BaseFtpFile;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.service.SecurityService;
import de.mfischbo.bustamail.template.service.TemplateService;

public class BustaFSView implements FileSystemView {

	private Authentication		auth;
	
	private TemplateService 	tService;

	private SecurityService		secService;
	
	private MediaService		mediaService;
	
	private BaseFtpDirectory	root;

	private BaseFtpDirectory	cwd;
	
	private OrgUnit				currentOwner;
	
	private List<Media>			_pending;
	
	public BustaFSView(TemplateService tService, SecurityService secService, MediaService mediaService, Authentication auth) {
		this.tService = tService;
		this.secService = secService;
		this.mediaService = mediaService;
		this.auth = auth;
		this._pending = new LinkedList<>();
		
		this.root = new BaseFtpDirectory("/", null, null);
		this.cwd = root;
	}
	
	private void navigateTo(String part) {
		if (part.equals(".") || part.isEmpty()) return;
			
		if (part.equals("..")) {
			if (this.cwd == this.root) return;
				
			this.cwd = this.cwd.getParent();
			return;
		}
			
		if (!this.cwd.isInitialized())
			this.cwd = initialize(this.cwd);
			
		for (FtpFile c : this.cwd.listFiles()) {
			if (c.getName().equals(part)) {
				this.cwd = (BaseFtpDirectory) c;
				this.currentOwner = this.cwd.getOwner();
				return;
			}
		}
	}

	@Override
	public boolean changeWorkingDirectory(String arg0) throws FtpException {
		SecurityContextHolder.getContext().setAuthentication(auth);
		String[] parts = arg0.split("/");

		if (arg0.startsWith("/")) {
			this.cwd = this.root;
		}

		if (parts.length == 0) {
			this.cwd = this.root;
			return true;
		}
		
		for (int i=0; i < parts.length; i++) {
			navigateTo(parts[i]);
		}
		return true;
	}

	@Override
	public void dispose() {
		System.out.println("Called dispose");
	}

	/**
	 * Called when the client requests an action (e.g. LIST) on a certain file.
	 * Requires the requested file to be fully initialized when returned
	 */
	@Override
	public FtpFile getFile(String arg0) throws FtpException {
		SecurityContextHolder.getContext().setAuthentication(this.auth);

		if (arg0.equals("./") || arg0.equals("/")) {
			if (!cwd.isInitialized()) {
				return initialize(cwd);
			} else {
				return cwd;
			}
		} else {
			for (FtpFile f : cwd.listFiles()) {
				if (f.getName().equals(arg0)) {
					return initialize((BaseFtpFile) f);
				}
			}
		}
		return null;
	}

	
	private BaseFtpDirectory initialize(BaseFtpDirectory dir) {
		
		// populate root
		if (this.cwd.getAbsolutePath().equals("/")) 
			return FtpFileFactory.populateRootDirectory(dir, secService.getOrgUnitsByCurrentUser());
		
		// populate intermediate directories
		if (this.cwd.getAbsolutePath().equals("/" + currentOwner.getName()))
			return FtpFileFactory.populateIntermediateDirs(dir);
		
		// populate subdirectories of media
		if (this.cwd.getAbsolutePath().startsWith("/" + currentOwner.getName() + "/media"))
			return FtpFileFactory.populateMediaDirectory(dir, currentOwner, mediaService);
		
		if (this.cwd.getAbsolutePath().startsWith("/" + currentOwner.getName() + "/templates")) {
			if (this.cwd.getAbsolutePath().equals("/" + currentOwner.getName() + "/templates"))
				return FtpFileFactory.populateTemplatesDirectory(dir, currentOwner, tService);
			else {
				return FtpFileFactory.populateTemplateDirectory(dir, currentOwner, tService, mediaService);
			}
		}
		
		return null;
	}
	
	private BaseFtpFile initialize(BaseFtpFile file) {
		return file;
	}
	
	public boolean persistPendingMedia() {
		Iterator<Media> mit = _pending.iterator();
		while (mit.hasNext()) {
			Media m = mit.next();
			try {
				mediaService.createMedia(m, mediaService.getDirectoryById(m.getDirectory()));
				mit.remove();
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}
		return true;
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
	
	public OrgUnit getCurrentOwner() {
		return this.currentOwner;
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
