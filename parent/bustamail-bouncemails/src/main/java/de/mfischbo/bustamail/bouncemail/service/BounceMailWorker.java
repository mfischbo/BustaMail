package de.mfischbo.bustamail.bouncemail.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import de.mfischbo.bustamail.bouncemail.domain.BounceAccount;
import de.mfischbo.bustamail.bouncemail.domain.BounceMail;
import de.mfischbo.bustamail.bouncemail.repo.BounceMailRepo;

public class BounceMailWorker {

	private BounceAccount account;
	
	private BounceMailRepo	bmRepo;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	public BounceMailWorker(BounceAccount account, BounceMailRepo bmRepo) {
		this.account = account;
		this.bmRepo = bmRepo;
	}
	
	@Async
	public void exec() {

		Properties props = account.getConnectionProperties();
	
		Session session = Session.getInstance(props);
		try {
			Store store = session.getStore(props.getProperty("mail.store.protocol"));
			
			log.debug("Connecting to store at host : " + account.getHostname());
			store.connect(account.getHostname(), account.getUsername(), account.getPassword());
			
			if (store.isConnected()) {
				log.debug("Connection successful");
				handleStore(store);
				
				log.debug("Closing connection...");
				store.close();
				log.debug("Connection successfully closed");
			}
		} catch (MessagingException ex) {
			log.error("Failed to process messages. Cause: " + ex.getMessage());
			log.error(ex.toString());
		} 
	}
	
	private void handleStore(Store store) throws MessagingException {
		
		Folder f = store.getFolder("INBOX");
		f.open(Folder.READ_WRITE);

		//Message[] messages = f.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
		Message[] messages = f.getMessages();
		
		// all messages are treated as new
		for (Message m : messages) {
			MimeMessage mm = (MimeMessage) m;
			
			BounceMail bm = bmRepo.findByMessageId(mm.getMessageID());
			if (bm == null)
				handleMessage((MimeMessage) m);
			else
				log.debug("Ignoring message " + mm.getMessageID() + " -> already read");
			
			if (account.isRemoveOnRead())
				mm.setFlag(Flags.Flag.DELETED, true);
		}
		f.close(true);
	}
	

	private void handleMessage(MimeMessage m) throws MessagingException {
		log.debug("Working on message : " + m.getMessageID());
	
		try {
			BounceMail mail = new BounceMail();
			mail.setMessageId(m.getMessageID());
			mail.setSubject(m.getSubject());
		
			mail.setDateSent(new DateTime(m.getSentDate()));
			mail.setDateReceived(new DateTime(m.getReceivedDate()));
			
			if (m.getSender() != null)
				mail.setSender(m.getSender().toString());
			
			for (Address a : m.getFrom()) 
				mail.getFrom().add(a.toString());
		
			// parse the X-BM-
		
			log.debug("Inserting message " + mail.getMessageId());
			bmRepo.save(mail);
		} catch (Exception ex) {
			log.warn("Unparseable message with id : " + m.getMessageID());
		}
	}
}