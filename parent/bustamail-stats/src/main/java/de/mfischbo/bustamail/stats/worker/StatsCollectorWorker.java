package de.mfischbo.bustamail.stats.worker;

import java.io.File;
import java.io.FileFilter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.mfischbo.bustamail.stats.domain.StatsEntry;
import de.mfischbo.bustamail.stats.domain.StatsEntry.RecordType;
import de.mfischbo.bustamail.stats.repository.StatsEntryRepo;

@Component
public class StatsCollectorWorker {

	@Inject Environment env;

	@Inject StatsEntryRepo	sRepo;
	
	private File baseDir;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@PostConstruct
	public void init() {
		this.baseDir = new File(env.getProperty("de.mfischbo.bustamail.mailer.batch.basedir"));
	}
	
	
	@Scheduled(fixedDelay=30000)
	public void executeStatsSorting() {
		
		log.debug("Executing stats collector");
		File[] jobs = this.baseDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File j) {
				if (j.isDirectory() && j.canRead()) {
					File running = new File(j.getAbsolutePath() + "/.running");
					return running.exists();
				}
				return false;
			}
		});
		
		log.debug("Found {} jobs to collect stats from", jobs.length);
		for (File f : jobs) {
			try {
				// setup and validate the environment
				File successDir = new File(f.getAbsolutePath() + "/.success");
				File failDir    = new File(f.getAbsolutePath() + "/.failed");
				ObjectId mailingId = new ObjectId(f.getAbsoluteFile().getName());
				
				if (!successDir.exists() || !successDir.canRead() || !failDir.exists() || !failDir.canRead())
					continue;
				
				File[] subs = successDir.listFiles();
				log.debug("Processing {} file from .success directory", subs.length);
				for (File s : subs) {
					StatsEntry e = new StatsEntry();
					e.setMailingId(mailingId);
					e.setSubscriptionId(new ObjectId(s.getAbsoluteFile().getName()));
					e.setType(RecordType.SENT_SUCCESS);
					e.setDateCreated(new DateTime(s.lastModified()));
					sRepo.save(e);
					s.delete();
				}
			
				subs = failDir.listFiles();
				log.debug("Processing {} files from .filure directory", subs.length);
				for (File s : subs) {
					StatsEntry e = new StatsEntry();
					e.setMailingId(mailingId);
					e.setSubscriptionId(new ObjectId(s.getAbsoluteFile().getName()));
					e.setDateCreated(new DateTime(s.lastModified()));
					e.setType(RecordType.SENT_FAILURE);
					sRepo.save(e);
					s.delete();
				}
			} catch (Exception ex) {
				log.error("Failed creating stats entries for jobfile {}. Cause: {}", f.getName(), ex.getMessage());
			}
		}
	}
}