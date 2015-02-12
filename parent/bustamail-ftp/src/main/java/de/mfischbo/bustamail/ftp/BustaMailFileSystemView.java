package de.mfischbo.bustamail.ftp;

import java.util.Set;

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;

import de.mfischbo.bustamail.ftp.domain.BustaFtpFile;
import de.mfischbo.bustamail.ftp.domain.RootFtpFile;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.service.SecurityService;
import de.mfischbo.bustamail.template.service.TemplateService;

public class BustaMailFileSystemView implements FileSystemView {

	private User user;
	
	private TemplateService 	tService;

	private SecurityService		secService;
	
	private BustaFtpFile	    root;

	private BustaFtpFile		cwd;
	
	public BustaMailFileSystemView(User user, TemplateService tService, SecurityService secService) {
		this.user = user;
		this.tService = tService;
		this.secService = secService;
		
		Set<OrgUnit> units = secService.getOrgUnitsByCurrentUser();
		this.root = new RootFtpFile(units);
		this.cwd  = this.root;
	}
	
	@Override
	public boolean changeWorkingDirectory(String arg0) throws FtpException {
		if (arg0.equals(".")) return true;
		
		if (arg0.equals("..")) {
			if (this.cwd != this.root) {
				this.cwd = this.cwd.getParent();
				return true;
			}
		}
		
		for (FtpFile f : this.cwd.listFiles()) {
			if (f.getName().equals(arg0)) {
				this.cwd = (BustaFtpFile) f;
				return true;
			}
		}
		return false;
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public FtpFile getFile(String arg0) throws FtpException {
		if (arg0.equals("/") || arg0.equals("./")) {
			return this.cwd;
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
		return true;
	}
}
