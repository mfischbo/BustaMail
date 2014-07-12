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
import org.springframework.context.annotation.PropertySource;
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

@Configuration
@EnableAutoConfiguration
@EnableScheduling
@EnableAsync
//@EnableSwagger
@ComponentScan("de.mfischbo")
@PropertySource("classpath:/bm-application.properties")
public class ApplicationConfig {
	
	@Inject
	Environment			env;
	
	@Bean(destroyMethod = "shutdown")
	public DataSource getBustamailCoreDS() {
		
		List<String> profiles = Arrays.asList(env.getActiveProfiles());
		
		String dbName = "bustamaildb";
		if (profiles.contains("testing"))
			dbName += "_test";
		
		HikariConfig c = new HikariConfig();
		c.setMaximumPoolSize(50);
		c.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		c.addDataSourceProperty("url", "jdbc:mysql://localhost:3306/" + dbName);
		c.addDataSourceProperty("user", "root");
		c.addDataSourceProperty("password", "matrix");
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
		freemarker.template.Configuration c = new freemarker.template.Configuration();
		c.setTemplateLoader(new StringTemplateLoader());
		fmc.setConfiguration(c);
		return fmc;
	}
}
