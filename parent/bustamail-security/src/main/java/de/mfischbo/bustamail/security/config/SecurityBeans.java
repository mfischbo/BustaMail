package de.mfischbo.bustamail.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;

import de.mfischbo.bustamail.security.service.ActorBasedPermissionEvaluator;

@Configuration
public class SecurityBeans {

	@Bean
	public DefaultMethodSecurityExpressionHandler getExpressionHandler() {
		DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		handler.setPermissionEvaluator(getPermissionEvaluator());
		return handler;
	}

	
	@Bean
	public ActorBasedPermissionEvaluator getPermissionEvaluator() {
		return new ActorBasedPermissionEvaluator();
	}
	
}
