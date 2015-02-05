package de.mfischbo.bustamail.mailer.processor;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;

public class JobFolderProcessingStep implements IMailingProcessorStep {

	public static final String		CNF_JOBDIR_KEY = "de.mfischbo.bustamail.mailer.batch.basedir";
	
	private Environment env;
	
	private Logger log = LoggerFactory.getLogger(getClass());

	public JobFolderProcessingStep(Environment env) {
		this.env = env;
	}
	
	@Override
	public LiveMailing process(LiveMailing mailing) throws BustaMailException {
		
		if (mailing.getMailingId() == null)
			throw new IllegalArgumentException("The mailing id must not be null");
		
		File jobFolder;
		try {
			log.info("Creating job folder for this mailing");
			jobFolder = new File(env.getProperty(CNF_JOBDIR_KEY) + "/" + mailing.getMailingId());
			if (jobFolder.exists()) {
				log.error("Found a job folder with the same matching path at : " + jobFolder.getAbsolutePath());
				log.error("This indicates that the publishing has been already triggered. Aborting this attempt!");
				throw new RuntimeException("Found a job folder with the same path at : " + jobFolder.getAbsolutePath());
			} else {
				jobFolder.mkdirs();
			}
			
			log.info("Creating subfolders for success/failed/retry mailings");
			boolean sub = true;
			File success = new File(jobFolder.getAbsolutePath() + "/.success");
			File failed  = new File(jobFolder.getAbsolutePath() + "/.failed");
			File retry   = new File(jobFolder.getAbsolutePath() + "/.retry");
			sub &= success.mkdir();
			sub &= failed.mkdir();
			sub &= retry.mkdir();
			
			if (!sub) {
				log.error("Failed to create success/failed/retry folders in job folder");
				jobFolder.delete();
				throw new BustaMailException("Failed creating the job folder structure");
			}
			return mailing;
		} catch (Exception ex) {
			log.error("Failed to create job output folder for mailing " + mailing.getMailingId());
			log.error("Cause: " + ex.getMessage());
			throw new BustaMailException("Failed creating the job folder structure");
		}
	}
}