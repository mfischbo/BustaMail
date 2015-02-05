package de.mfischbo.bustamail.mailer.processor;

import java.io.File;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.StreamUtils;

import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;

public class FilePublisherStep implements IMailingProcessorStep {

	static final String DOC_ROOT_KEY = "de.mfischbo.bustamail.env.apache.documentRoot";
	
	private Environment		env;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	public FilePublisherStep(Environment env) {
		this.env = env;
	}
	
	@Override
	public LiveMailing process(LiveMailing mailing) throws BustaMailException {

		if (mailing.getResources() == null) {
			log.debug("No publishable resources found on this mailing. Returning without operation.");
			return mailing;
		}
		
		if (mailing.getMailingId() == null)
			throw new IllegalArgumentException("The mailing id must not be null");
		
		File resourceRoot = new File(env.getProperty(DOC_ROOT_KEY) + "/mailing_" + mailing.getMailingId());
		if (!resourceRoot.exists())
			resourceRoot.mkdirs();

		mailing.getResources().keySet().forEach(k -> {
			try {
				File f = new File(resourceRoot.getAbsolutePath() + "/" + k.toString());
				FileOutputStream fOut = new FileOutputStream(f);
				StreamUtils.copy(mailing.getResources().get(k), fOut);
				fOut.flush();
				fOut.close();
			} catch (Exception ex) {
				log.error("Failed to publish resource for id {}. Cause : {}", k.toString(), ex.getMessage());
			}
		});
		return mailing;
	}
}
