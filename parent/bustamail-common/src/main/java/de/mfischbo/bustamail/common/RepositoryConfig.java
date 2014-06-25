package de.mfischbo.bustamail.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
/*
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory"/*, 
	basePackages = "de.mfischbo.bustamail", 
	includeFilters = {
		@ComponentScan.Filter(value = JpaRepository.class, type = FilterType.ASSIGNABLE_TYPE)
	})
@EnableTransactionManagement
*/
public class RepositoryConfig {

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
