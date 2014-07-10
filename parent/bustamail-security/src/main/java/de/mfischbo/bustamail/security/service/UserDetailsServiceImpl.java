package de.mfischbo.bustamail.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.security.repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserRepository		userRepo;

	@Override
	public UserDetails loadUserByUsername(String arg0)
			throws UsernameNotFoundException {

		try {
			User u = userRepo.findByEmail(arg0);
			if (u == null)
				throw new UsernameNotFoundException("Unknown user");
			
			return u;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new UsernameNotFoundException(ex.getMessage());
		}
	}
}
