package de.mfischbo.bustamail.ftp;

import javax.inject.Inject;

import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.springframework.stereotype.Component;

import de.mfischbo.bustamail.security.service.SecurityService;
import de.mfischbo.bustamail.template.service.TemplateService;

@Component
public class BustaMailFileSystemFactory implements FileSystemFactory {

	@Inject private TemplateService tService;
	
	@Inject private SecurityService	secService;
	
	@Override
	public FileSystemView createFileSystemView(User arg0) throws FtpException {
		return new BustaMailFileSystemView(arg0, tService, secService);
	}
}
