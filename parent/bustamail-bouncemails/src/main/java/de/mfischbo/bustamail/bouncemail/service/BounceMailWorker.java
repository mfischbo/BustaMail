package de.mfischbo.bustamail.bouncemail.service;

import java.util.Properties;

import javax.mail.Session;
import javax.mail.Store;

import org.springframework.scheduling.annotation.Async;

import de.mfischbo.bustamail.bouncemail.domain.BounceAccount;
import de.mfischbo.bustamail.bouncemail.domain.BounceAccount.AccountType;
import de.mfischbo.bustamail.bouncemail.repo.BounceAccountRepo;

public class BounceMailWorker {

	private BounceAccount account;
	
	private BounceAccountRepo	baRepo;
	
	public BounceMailWorker(BounceAccount account, BounceAccountRepo baRepo) {
		this.account = account;
		this.baRepo = baRepo;
	}
	
	@Async
	public void exec() {

		Properties props = new Properties();
		
		if (account.getAccountType() == AccountType.POP3) {
			if (account.isUseSSL()) props.put("mail.store.protocol", "pop3s");
			if (!account.isUseSSL()) props.put("mail.store.protocol", "pop3");
		} else if (account.getAccountType() == AccountType.IMAP) {
			if (account.isUseSSL()) props.put("mail.store.protocol", "imaps");
			if (!account.isUseSSL()) props.put("mail.store.protocol", "imap");
		}
	
		Session session = Session.getDefaultInstance(props);
		try {
			Store store = session.getStore(props.getProperty("mail.store.protocol"));
			store.connect(account.getHostname(), account.getUsername(), account.getPassword());
		} catch (Exception ex) {
			
		}
	}
}

