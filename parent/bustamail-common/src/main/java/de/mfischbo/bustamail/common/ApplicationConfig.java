package de.mfischbo.bustamail.common;

import javax.inject.Inject;
import javax.servlet.MultipartConfigElement;

import org.bson.types.ObjectId;
import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import de.mfischbo.bustamail.serializer.ObjectIdSerializer;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Version;

@Configuration
//@EnableAutoConfiguration
@EnableScheduling
@EnableAsync
//@EnableSwagger
@ComponentScan("de.mfischbo.bustamail")
//@EnableMongoRepositories(basePackages = {"de.mfischbo.bustamail"})
//@PropertySource("classpath:/bm-application.properties")
public class ApplicationConfig {
	
	@Inject
	Environment			env;

	@Bean
	public Module getJacksonJodaModule() {
		return new JodaModule();
	}
	
	@Bean
	public Module getObjectIdSerializer() {
		SimpleModule sm = new SimpleModule();
		sm.addSerializer(ObjectId.class, new ObjectIdSerializer());
		return sm;
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
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize("10MB");
		return factory.createMultipartConfig();
	}
	
	@Bean
	public FreeMarkerConfigurer getFreeMarkerConfigurer() {
		FreeMarkerConfigurer fmc = new FreeMarkerConfigurer();
		freemarker.template.Configuration c = new freemarker.template.Configuration(new Version("2.3.21"));
		c.setTemplateLoader(new StringTemplateLoader());
		fmc.setConfiguration(c);
		return fmc;
	}
}