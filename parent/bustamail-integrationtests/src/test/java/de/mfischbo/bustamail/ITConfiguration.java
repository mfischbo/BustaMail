package de.mfischbo.bustamail;

import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;

import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@PropertySource("classpath:/bm-application.properties")
public class ITConfiguration {

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
	
	@Bean(destroyMethod = "shutdown")
	public DataSource getBustamailCoreDS() {
		
		HikariConfig c = new HikariConfig();
		c.setMaximumPoolSize(50);
		c.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		c.addDataSourceProperty("url", "jdbc:mysql://localhost:3306/bustamaildb_test");
		c.addDataSourceProperty("user", "root");
		c.addDataSourceProperty("password", "matrix");
		return new HikariDataSource(c);
	}
}