package de.mfischbo.bustamail.itest;

import java.util.UUID;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mfischbo.bustamail.ITConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ITConfiguration.class})
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("testing")
public class AbstractIntegrationTestBase {

	@Inject
	private FilterChainProxy 			chain;
	
	@Inject
	protected ObjectMapper				mapper;
	
	@Inject
	private WebApplicationContext		wac;
	
	@Inject
	private AuthenticationManager		authMan;

	private Authentication				auth;
	
	private MockHttpSession				session;
	
	protected MockMvc					mock;

	// base path to test against
	protected static final String BASE = "http://localhost:8080";

	// return type to expect
	protected static final MediaType   mtJson = MediaType.parseMediaType("application/json;charset=UTF-8");
	
	// UUID of the testing org unit
	protected static final ObjectId TEST_OWNER_ID = new ObjectId("");//UUID.fromString("223f9d9d-473b-9d9d-781e-1c9d615f0000");
	
	// UUID of the user with full permissions
	protected static final UUID TEST_USER_FULL_PERMS = UUID.fromString(toUUIDString("6424309D169D42059D145D9D3E9D9D9D"));
	
	@Before
	public void setup() {
	
		mock = MockMvcBuilders
				.webAppContextSetup(wac)
				.addFilters(chain)
				.build();
	}

	/**
	 * Returns a session with an authenticated user having all permission for the test org unit
	 * @return The session
	 */
	protected MockHttpSession getPermittedSession() {
		auth  = authMan.authenticate(new UsernamePasswordAuthenticationToken("schdahle@art-ignition.de", "test"));
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		session = new MockHttpSession();
		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
		return session;
	}

	/**
	 * Returns a session with authentication of a user having no permissions on all actions
	 * @return The session
	 */
	protected MockHttpSession getUnpermittedSession() {
		auth  = authMan.authenticate(new UsernamePasswordAuthenticationToken("testaccount@art-ignition.de", "test"));
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		session = new MockHttpSession();
		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
		return session;
	}

	/**
	 * Returns a session with authentication from a foreign org unit user
	 * @return The session
	 */
	protected MockHttpSession getForeignSession() {
		auth = authMan.authenticate(new UsernamePasswordAuthenticationToken("m.fischboeck@googlemail.com", "test"));
		session = new MockHttpSession();
		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
		return session;
	}


	protected static String toUUIDString(String hexString) {
		String retval = hexString.toLowerCase();
		retval = retval.substring(0, 8) + "-" 
				+ retval.substring(8,12) + "-" 
				+ retval.substring(12,16) + "-" 
				+ retval.substring(16,20) + "-" 
				+ retval.substring(20);
		return retval;
	}
}
