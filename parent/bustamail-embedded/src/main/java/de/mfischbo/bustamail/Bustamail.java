package de.mfischbo.bustamail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import de.mfischbo.bustamail.common.ApplicationConfig;
import de.mfischbo.bustamail.security.config.SecurityBeans;
import de.mfischbo.bustamail.security.config.SecurityConfig;

@Configuration
@EnableAutoConfiguration
@ComponentScan("de.mfischbo.bustamail")
@PropertySource("application-dev.properties")
@Import({ApplicationConfig.class, SecurityConfig.class, SecurityBeans.class})
public class Bustamail extends SpringBootServletInitializer {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Bustamail.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder app) {
		SpringApplicationBuilder retval = app.sources(Bustamail.class);
		return retval.profiles("dev");
		//return retval;
	}
}
