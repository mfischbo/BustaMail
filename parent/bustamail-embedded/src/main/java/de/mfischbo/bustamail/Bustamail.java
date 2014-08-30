package de.mfischbo.bustamail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

import de.mfischbo.bustamail.common.ApplicationConfig;

@EnableAutoConfiguration
@Import({ApplicationConfig.class})
public class Bustamail extends SpringBootServletInitializer {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Bustamail.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder app) {
		return app.sources(Bustamail.class);
	}
}
