package de.mfischbo.bustamail.stats.service;

import org.springframework.security.access.prepost.PreAuthorize;

import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.stats.domain.StatsEntry;
import de.mfischbo.bustamail.stats.dto.MailingStats;

public interface StatsService {

	public StatsEntry createStatsEntry(StatsEntry e);

	@PreAuthorize("hasPermission(#m.owner, 'Mailings.USE_MAILINGS')")
	public MailingStats getStatsByMailing(Mailing m);
}
