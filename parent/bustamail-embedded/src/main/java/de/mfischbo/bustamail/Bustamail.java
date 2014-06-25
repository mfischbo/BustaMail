package de.mfischbo.bustamail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import de.mfischbo.bustamail.common.ApplicationConfig;
import de.mfischbo.bustamail.common.RepositoryConfig;
import de.mfischbo.bustamail.security.config.SecurityBeans;
import de.mfischbo.bustamail.security.config.SecurityConfig;

@Configuration
@EnableAutoConfiguration
@ComponentScan("de.mfischbo.bustamail")
@Import({ApplicationConfig.class, RepositoryConfig.class, SecurityConfig.class, SecurityBeans.class})
public class Bustamail {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Bustamail.class, args);
	}
}
