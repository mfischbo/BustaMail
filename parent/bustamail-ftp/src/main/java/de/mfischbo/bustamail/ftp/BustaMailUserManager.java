package de.mfischbo.bustamail.ftp;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.springframework.stereotype.Component;

import de.mfischbo.bustamail.security.dto.AuthenticationDTO;
import de.mfischbo.bustamail.security.dto.UserDTO;
import de.mfischbo.bustamail.security.repository.UserRepository;
import de.mfischbo.bustamail.security.service.SecurityService;

@Component
public class BustaMailUserManager implements UserManager {

	@Inject private SecurityService secService;
	
	@Inject private UserRepository userRepo;
	
	private Map<String, BustaMailFtpUser> authenticatedUsers = new HashMap<>();
	
	@Override
	public User authenticate(Authentication arg0)
			throws AuthenticationFailedException {
		if (arg0 instanceof UsernamePasswordAuthentication) {
			UsernamePasswordAuthentication auth = (UsernamePasswordAuthentication) arg0;
			de.mfischbo.bustamail.security.domain.User retval = userRepo.findByEmail(auth.getUsername());
			if (retval != null) {
				AuthenticationDTO bmAuth = new AuthenticationDTO();
				bmAuth.setEmail(retval.getEmail());
				bmAuth.setPassword(auth.getPassword());
				UserDTO r = secService.signIn(bmAuth);
				if (r != null) {
					BustaMailFtpUser bmFtpUser = new BustaMailFtpUser(retval);
					this.authenticatedUsers.put(bmAuth.getEmail(), bmFtpUser);
					return bmFtpUser;
				}
			}
		}
		return null;
	}

	@Override
	public void delete(String arg0) throws FtpException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean doesExist(String arg0) throws FtpException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getAdminName() throws FtpException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getAllUserNames() throws FtpException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUserByName(String arg0) throws FtpException {
		return this.authenticatedUsers.get(arg0);
	}

	@Override
	public boolean isAdmin(String arg0) throws FtpException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void save(User arg0) throws FtpException {
		// TODO Auto-generated method stub

	}

}
