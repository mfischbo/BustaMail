package de.mfischbo.bustamail.mailer.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import freemarker.template.TemplateExceptionHandler;

@Configuration
public class MailerConfiguration {

	@Bean
	public JavaMailSender getSimpleMailSender() {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost("art-ignition.de");
		sender.setUsername("testaccount@art-ignition.de");
		sender.setPassword("hellonasty");
		sender.setPort(25);
		
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.auth", "true");
		props.put("mail.host", "art-ignition.de");
		props.put("mail.user", "testaccount@art-ignition.de");
		props.put("mail.password", "hellonasty");
		props.put("mail.smtps.auth", "true");
		props.put("mail.smtp.auth", "true");
		sender.setJavaMailProperties(props);
		
		return sender;
	}
	
	@Bean
	public freemarker.template.Configuration getConfiguration() {
		freemarker.template.Configuration c = new freemarker.template.Configuration();
		c.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
		return c;
	}
}
