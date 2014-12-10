package de.mfischbo.bustamail.common;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;

import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Version;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
@EnableAsync
//@EnableSwagger
@ComponentScan("de.mfischbo")
//@PropertySource("classpath:/bm-application.properties")
public class ApplicationConfig {
	
	@Inject
	Environment			env;
	
	@Bean(destroyMethod = "shutdown")
	public DataSource getBustamailCoreDS() {
		
		List<String> profiles = Arrays.asList(env.getActiveProfiles());
		
		String dbName = env.getProperty("de.mfischbo.bustamail.db.ds.name");
		if (profiles.contains("testing"))
			dbName = env.getProperty("de.mfischbo.bustamail.db.ds.testName");
		
		HikariConfig c = new HikariConfig();
		c.setMaximumPoolSize(50);
		c.setDataSourceClassName(env.getProperty("de.mfischbo.bustamail.db.ds.classname"));
		c.addDataSourceProperty("url", env.getProperty("de.mfischbo.bustamail.db.ds.url")+ dbName);
		c.addDataSourceProperty("user", env.getProperty("de.mfischbo.bustamail.db.ds.user"));
		c.addDataSourceProperty("password", env.getProperty("de.mfischbo.bustamail.db.ds.password"));
		return new HikariDataSource(c);
	}
	
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
