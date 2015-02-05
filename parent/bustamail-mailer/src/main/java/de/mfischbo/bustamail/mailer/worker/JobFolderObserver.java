package de.mfischbo.bustamail.mailer.worker;

import java.io.File;
import java.io.FileFilter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mfischbo.bustamail.mailer.dto.SMTPConfiguration;
import de.mfischbo.bustamail.mailer.processor.JobFolderProcessingStep;

@Component
public class JobFolderObserver {

	@Inject
	Environment		env;
	
	@Inject
	ObjectMapper	mapper;

	private File	jobBasedir;
	
	private Logger  log = LoggerFactory.getLogger(getClass());

	/* indicates whether this is the initial run of the observer */
	private boolean	initialRun = true;
	
	private SMTPConfiguration fallbackConfig;
	
	@PostConstruct
	public void afterPropertiesSet() {
		this.jobBasedir = new File(env.getProperty(JobFolderProcessingStep.CNF_JOBDIR_KEY));
		this.fallbackConfig = new SMTPConfiguration(env);
	}
	
	
	@Scheduled(fixedRate=30000)
	void observeJobFolders() {
		log.debug("Executing JobFolderObserver on directory {}", this.jobBasedir.getAbsolutePath());
		File[] jobs = this.jobBasedir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File j) {
				if (j.isDirectory() && j.canRead()) {
					File activationFile = new File(j.getAbsolutePath() + "/.activate");
					File runningFile    = new File(j.getAbsoluteFile() + "/.running");
					
					if (initialRun)
						return activationFile.exists();
					else
						return (activationFile.exists() && !runningFile.exists());
				} else return false;
			}
		});
		
		log.debug("Found {} potential matches", jobs.length);
		if (initialRun) {
			runInitialCleanup(jobs);
			initialRun = false;
		}
		
		// check if the mailer is enabled or not
		Boolean enabled = env.getProperty("de.mfischbo.bustamail.mailer.enabled", Boolean.class);
		if (!enabled)
			return;
	
		// execute a BatchMailWorker for every directory
		if (jobs != null) {
			for (File j : jobs) {
				BatchMailWorker w = new BatchMailWorker(j, mapper, this.fallbackConfig);
				w.execute();
			}
		}
	}
	
	
	/**
	 * Runs a cleanup when the mailer has had an unexpected shutdown.
	 * This method removes all .running file markers from the job directories
	 * @param jobs
	 */
	private void runInitialCleanup(File[] jobs) {
		for (File f : jobs) {
			File marker = new File(f.getAbsolutePath() + "/.running");
			if (marker.exists()) {
				log.info("Removing .running marker from job directory {}", f.getAbsolutePath());
				marker.delete();
			}
		}
	}
}
