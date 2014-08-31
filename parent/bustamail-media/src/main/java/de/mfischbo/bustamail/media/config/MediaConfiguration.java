package de.mfischbo.bustamail.media.config;

import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MediaConfiguration {

	@Bean
	public Tika getTika() {
		return new Tika();
	}
	
}
