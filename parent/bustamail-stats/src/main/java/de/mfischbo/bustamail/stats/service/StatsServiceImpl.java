package de.mfischbo.bustamail.stats.service;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.stats.domain.StatsEntry;
import de.mfischbo.bustamail.stats.dto.MailingStats;
import de.mfischbo.bustamail.stats.repository.StatsEntryRepo;

@Service
public class StatsServiceImpl extends BaseService implements StatsService {

	@Inject 
	private StatsEntryRepo		sRepo;
	
	@Override
	public StatsEntry createStatsEntry(StatsEntry e) {
		return sRepo.save(e);
	}

	@Override
	public MailingStats getStatsByMailing(Mailing m) {
		MailingStats retval = new MailingStats();
		
		retval.setOpeningAmount(sRepo.getOpeningsByMailingId(m.getId()));
		retval.setUniqueOpeningAmount(sRepo.getUniqueOpeningsByMailing(m.getId()));
		
		return retval;
	}
}
