package de.mfischbo.bustamail.common;

import javax.servlet.MultipartConfigElement;

import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.MultiPartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mangofactory.swagger.plugin.EnableSwagger;

@Configuration
@EnableAutoConfiguration
@ComponentScan("de.mfischbo.bustamail")
@EnableSwagger
@PropertySource("classpath:/bm-application.properties")
public class ApplicationConfig {

	@Bean
	public Module getJacksonJodaModule() {
		return new JodaModule();
	}
	
	@Bean
	public DozerBeanMapperFactoryBean getDozerMapper() {
		DozerBeanMapperFactoryBean retval = new DozerBeanMapperFactoryBean();
		Resource[] mappingFiles = new Resource[1];
		mappingFiles[0] = new ClassPathResource("/dozer-mapping.xml");
		retval.setMappingFiles(mappingFiles);
		return retval;
	}
	
	@Bean
	public MultipartConfigElement getMultipartResolver() {
		MultiPartConfigFactory factory = new MultiPartConfigFactory();
		factory.setMaxFileSize("10MB");
		return factory.createMultipartConfig();
	}
}
