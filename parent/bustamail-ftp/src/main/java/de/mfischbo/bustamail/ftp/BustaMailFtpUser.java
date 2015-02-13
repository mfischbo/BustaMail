package de.mfischbo.bustamail.ftp;

import java.util.List;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.User;

public class BustaMailFtpUser implements User {

	private de.mfischbo.bustamail.security.domain.User user;
	private String plainPass;
	
	public BustaMailFtpUser(de.mfischbo.bustamail.security.domain.User user, String plainPass) {
		this.user = user;
		this.plainPass = plainPass;
	}
	
	@Override
	public AuthorizationRequest authorize(AuthorizationRequest arg0) {
		return arg0;
	}

	@Override
	public List<Authority> getAuthorities() {
		return null;
	}

	@Override
	public List<Authority> getAuthorities(Class<? extends Authority> arg0) {
		return null;
	}

	@Override
	public boolean getEnabled() {
		return !user.isLocked();
	}

	@Override
	public String getHomeDirectory() {
		return "/";
	}

	@Override
	public int getMaxIdleTime() {
		return 0;
	}

	@Override
	public String getName() {
		return this.user.getEmail();
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}
	
	public String getPlainPass() {
		return this.plainPass;
	}

}
