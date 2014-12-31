package de.mfischbo.bustamail.bouncemail.service;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.mfischbo.bustamail.bouncemail.domain.BounceAccount;
import de.mfischbo.bustamail.bouncemail.repo.BounceAccountRepo;
import de.mfischbo.bustamail.bouncemail.repo.BounceMailRepo;

/**
 * Scheduler class that handles the polling of bounce accounts on a fixed rate.
 * @author M. Fischboeck
 *
 */
@Component
public class BounceAccountObserver {

	@Inject
	private BounceAccountRepo	baRepo;
	
	@Inject
	private BounceMailRepo		bmRepo;

	private Long				lastExecTime = null;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Scheduled(fixedRate = 30000)
	void observeAccounts() {
		
		log.debug("Executing bounce account observer");
		
		List<BounceAccount> accounts = baRepo.findAllEnabled();
	
		log.debug("Found ["+accounts.size()+"] potential candidates to be checked for polling");
		for (BounceAccount account : accounts) {
			
			if (lastExecTime == null) {
			
				log.debug("Launching worker on account " + account.getName());
				log.debug("Caused by lastExecTime being null");
				
				BounceMailWorker worker = new BounceMailWorker(account, bmRepo);
				worker.exec();
			} else {
				if ((System.currentTimeMillis() - this.lastExecTime) > (account.getPollInterval() * 1000)) {
			
					log.debug("Launching worker on account " + account.getName());
					log.debug("Caused by exeeding poll interval");
					
					BounceMailWorker worker = new BounceMailWorker(account, bmRepo);
					worker.exec();
				}
			}
		}
		this.lastExecTime = System.currentTimeMillis();
	}
}