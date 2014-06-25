package de.mfischbo.bustamail.itest;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;

import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.MultiPartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableAutoConfiguration
@ComponentScan("de.mfischbo")
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory", basePackages = "de.mfischbo.bustamail")
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
		MultiPartConfigFactory factory = new MultiPartConfigFactory();
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
	
	@Bean(name = "entityManagerFactory")
	public EntityManagerFactory getEntityManagerFactory() {
		
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setShowSql(false);
		adapter.setGenerateDdl(false);
		adapter.setDatabasePlatform(MySQL5InnoDBDialect.class.getName());
		
		LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
		lcemfb.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		lcemfb.setJpaVendorAdapter(adapter);
		lcemfb.setDataSource(getBustamailCoreDS());
		lcemfb.setPersistenceUnitName("Bustamail_Core_PU");
		lcemfb.setPackagesToScan("de.mfischbo.bustamail");
	
		
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("jadira.usertype.autoRegisterUserTypes", "true");
		props.put("jadira.usertype.databaseZone", "UTC");
		props.put("jadira.usertype.javaZone", "UTC");
		
		props.put("hibernate.connection.release_mode", "on_close");
		props.put("hibernate.connection.CharSet", "utf8");
		props.put("hibernate.connection.characterEncoding", "utf8");
		props.put("hibernate.connection.useUnicode", true);
		lcemfb.setJpaPropertyMap(props);
		
		lcemfb.afterPropertiesSet();
		return lcemfb.getObject();
	}
}
