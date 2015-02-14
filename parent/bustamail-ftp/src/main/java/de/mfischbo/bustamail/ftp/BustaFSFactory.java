package de.mfischbo.bustamail.ftp;

import javax.inject.Inject;

import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import de.mfischbo.bustamail.media.service.MediaService;
import de.mfischbo.bustamail.security.dto.AuthenticationDTO;
import de.mfischbo.bustamail.security.service.SecurityService;
import de.mfischbo.bustamail.template.service.TemplateService;

@Component
public class BustaFSFactory implements FileSystemFactory {

	@Inject private TemplateService tService;
	
	@Inject private SecurityService	secService;
	
	@Inject private MediaService	mediaService;
	
	@Override
	public FileSystemView createFileSystemView(User arg0) throws FtpException {
		
		// we need to authenticate again, since the injected services are not aware of the user is being logged in
		BustaMailFtpUser user = (BustaMailFtpUser) arg0;
		AuthenticationDTO auth = new AuthenticationDTO();
		auth.setEmail(user.getName());
		auth.setPassword(user.getPlainPass());
		secService.signIn(auth);
		
		Authentication ctxAuth = SecurityContextHolder.getContext().getAuthentication();
		
		return new BustaFSView(tService, secService, mediaService, ctxAuth);
	}
}
