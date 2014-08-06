package de.mfischbo.bustamail.mailer.worker;

import java.io.File;
import java.io.FileFilter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mfischbo.bustamail.mailer.dto.SMTPConfiguration;
import de.mfischbo.bustamail.mailer.service.SimpleMailServiceImpl;

@Component
public class JobFolderObserver {

	@Inject
	Environment		env;
	
	@Inject
	ObjectMapper	mapper;

	private File	jobBasedir;
	
	private SMTPConfiguration fallbackConfig;
	
	@PostConstruct
	public void afterPropertiesSet() {
		this.jobBasedir = new File(env.getProperty(SimpleMailServiceImpl.CNF_JOBDIR_KEY));
		this.fallbackConfig = new SMTPConfiguration(env);
	}
	
	
	@Scheduled(fixedRate=5000)
	void observeJobFolders() {
		File[] jobs = this.jobBasedir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File j) {
				if (j.isDirectory() && j.canRead()) {
					File activationFile = new File(j.getAbsolutePath() + "/.activate");
					File runningFile    = new File(j.getAbsoluteFile() + "/.running");
					return (activationFile.exists() && !runningFile.exists());
				} else return false;
			}
		});
		
		if (jobs != null) {
			for (File j : jobs) {
				BatchMailWorker w = new BatchMailWorker(j, mapper, this.fallbackConfig);
				w.execute();
			}
		}
	}
}
