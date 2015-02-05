package de.mfischbo.bustamail.mailer.processor;

import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;

public class PreviewMailingProcessorStep implements IMailingProcessorStep {

	@Override
	public LiveMailing process(LiveMailing mailing) throws BustaMailException {
	
		mailing.setEnableLinkTracking(false);
		mailing.setEnableOpeningTracking(false);
		return mailing;
	}
}
