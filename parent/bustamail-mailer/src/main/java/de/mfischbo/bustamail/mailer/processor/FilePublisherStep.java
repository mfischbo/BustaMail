package de.mfischbo.bustamail.mailer.processor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.StreamUtils;
import org.zeroturnaround.zip.commons.FileUtils;

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

		if (mailing.getMailingId() == null)
			throw new IllegalArgumentException("The mailing id must not be null");
		
		File resourceRoot = new File(env.getProperty(DOC_ROOT_KEY) + "/mailing_" + mailing.getMailingId());
		if (!resourceRoot.exists())
			resourceRoot.mkdirs();
		
		// publish the blank.gif file
		if (mailing.isSpanCellReplacement()) {
			try {
				InputStream in = getClass().getResourceAsStream("/blank.gif");
				File blank = new File(resourceRoot.getAbsolutePath() + "/blank.gif");
				FileUtils.copy(in, blank);
			} catch (Exception ex) {
				log.error("Unable to copy blank.gif file.");
				throw new BustaMailException("Unable to copy blank.gif file");
			}
		}
		
		// publish the content as index.html to be displayed static
		try {
			File staticFile = new File(resourceRoot.getAbsolutePath() + "/index.html");
			FileUtils.copy(new ByteArrayInputStream(mailing.getHtmlContent().getBytes()), staticFile);
		} catch (Exception ex) {
			log.error("Unable to publish static file. Cause: {}", ex.getMessage());
			throw new BustaMailException("Unable to publish mailings static file");
		}

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
