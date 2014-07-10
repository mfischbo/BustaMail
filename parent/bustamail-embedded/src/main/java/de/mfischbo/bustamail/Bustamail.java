package de.mfischbo.bustamail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

import de.mfischbo.bustamail.common.ApplicationConfig;

@EnableAutoConfiguration
@Import({ApplicationConfig.class})
public class Bustamail {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Bustamail.class, args);
	}
}
