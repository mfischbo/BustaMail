package de.mfischbo.bustamail.mailer.processor;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;

public class JobActivatorStep implements IMailingProcessorStep {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private Environment env;
	
	public JobActivatorStep(Environment env) {
		this.env = env;
	}
	
	@Override
	public LiveMailing process(LiveMailing mailing) throws BustaMailException {
		
		File jobFolder = new File(
				env.getProperty(JobFolderProcessingStep.CNF_JOBDIR_KEY) + "/" + mailing.getMailingId());
		if (!jobFolder.exists())
			throw new IllegalStateException("Unable to find job folder "+ jobFolder.getAbsolutePath() +" to activate.");
			
		File activation = new File(jobFolder.getAbsolutePath() + "/.activate");
		if (!activation.exists()) {
			try {
				activation.createNewFile();
				log.info("Created activation file to trigger the mailer start mailing");
			} catch (IOException ex) {
				throw new BustaMailException("Unable to create .activation file in " + jobFolder.getAbsolutePath());
			}
		}
		return mailing;
	}
}
