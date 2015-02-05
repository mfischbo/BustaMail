package de.mfischbo.bustamail.mailer.processor;

import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;

public interface IMailingProcessorStep {

	public LiveMailing process(LiveMailing mailing) throws BustaMailException;
}
