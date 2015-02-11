package de.mfischbo.bustamail.mailer.processor;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;
import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;
import de.mfischbo.bustamail.mailer.util.MailingSerializer;

public class MailingSerializerProcessorStep implements IMailingProcessorStep {

	private static final String		CNF_S_FAIL_KEY = "de.mfischbo.bustamail.mailer.batch.failOnSerializationFailure";

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private Environment env;
	
	private MailingSerializer serializer;
	
	private PersonalizerStep personalizer;
	
	public MailingSerializerProcessorStep(Environment env, PersonalizerStep personalizer, MailingSerializer serializer) {
		this.env = env;
		this.serializer = serializer;
		this.personalizer = personalizer;
	}
	
	@Override
	public LiveMailing process(LiveMailing mailing) throws BustaMailException {

		if (mailing.getMailingId() == null)
			throw new IllegalArgumentException("The mailing id must not be null");
	
		if (mailing.getRecipients() == null)
			throw new IllegalArgumentException("The mailings recipients list must not be null");
		
		if (mailing.getSubject() == null || mailing.getSubject().trim().length() == 0)
			throw new IllegalArgumentException("The subject of the mailing must not be null");
		
		if (mailing.getSenderAddress() == null)
			throw new IllegalArgumentException("The sender address must not be null");
		
		if (mailing.getReplyToAddress() == null)
			throw new IllegalArgumentException("The reply to address must not be null");
	
		
		boolean failFast = Boolean.parseBoolean(env.getProperty(CNF_S_FAIL_KEY));
		File jobFolder = new File(env.getProperty(JobFolderProcessingStep.CNF_JOBDIR_KEY) + "/" +  mailing.getMailingId());
		
		try {
			for (PersonalizedEmailRecipient r : mailing.getRecipients()) {
				String html = personalizer.processHTML(mailing, r);
				String text = personalizer.processText(mailing, r);
				
				boolean success = 
						serializer.serializeMailing(jobFolder, mailing, html, text, r);
				if (!success) {
					log.warn("Failed to serialize mailing for recipient: " + r.getEmail());
					if (failFast) {
						log.error("Aborting scheduling of mailing due to previous warning since fail fast is set to true");
						throw new BustaMailException("Failed serializing mailing. Fail fast is active");
					}
				}
			}
		} catch (Exception ex) {
			log.error("Problem occured during mailing serialization. Cause: " + ex.getMessage());
			throw new BustaMailException("Failed serializing mailing");
		}
		return mailing;
	}
}
