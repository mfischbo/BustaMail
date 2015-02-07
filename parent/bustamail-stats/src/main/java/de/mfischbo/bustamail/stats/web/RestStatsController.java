package de.mfischbo.bustamail.stats.web;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.mailing.service.MailingService;
import de.mfischbo.bustamail.stats.dto.MailingStats;
import de.mfischbo.bustamail.stats.service.StatsService;

@RestController
@RequestMapping(value = "/api/stats/", produces = "application/json")
public class RestStatsController extends BaseApiController {

	@Inject StatsService		service;
	@Inject MailingService		mService;
	
	@RequestMapping(value = "/{mid}", method = RequestMethod.GET)
	public MailingStats getStatsByMailingId(@PathVariable("mid") ObjectId mailingId) throws EntityNotFoundException {
		Mailing m = mService.getMailingById(mailingId);
		if (!m.isPublished())
			return null;
		return service.getStatsByMailing(m);
	}
}
