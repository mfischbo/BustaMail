package de.mfischbo.bustamail.publicapi.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.ApiException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailer.dto.LiveMailing;
import de.mfischbo.bustamail.mailer.processor.MailingPreProcessor;
import de.mfischbo.bustamail.mailer.service.SimpleMailService;
import de.mfischbo.bustamail.mailinglist.domain.Subscription;
import de.mfischbo.bustamail.mailinglist.domain.Subscription.SourceType;
import de.mfischbo.bustamail.mailinglist.domain.Subscription.State;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.repository.SubscriptionListRepository;
import de.mfischbo.bustamail.mailinglist.repository.SubscriptionRepository;
import de.mfischbo.bustamail.optin.domain.OptinMail;
import de.mfischbo.bustamail.optin.repository.OptinMailRepo;
import de.mfischbo.bustamail.publicapi.dto.PublicSubscriber;
import de.mfischbo.bustamail.publicapi.dto.PublicSubscriber.PubSubscription;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.repository.OrgUnitRepository;
import de.mfischbo.bustamail.subscriber.domain.Contact;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;
import de.mfischbo.bustamail.subscriber.repository.ContactRepository;
import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.repo.VersionedContentRepository;

@Service
public class PublicAPIServiceImpl extends BaseService implements PublicAPIService {

	@Inject Environment					env;
	
	@Inject	SubscriptionListRepository 	mlRepo;
	
	@Inject OrgUnitRepository			orgUnitRepo;
	
	@Inject SubscriptionRepository		subRepo;
	
	@Inject ContactRepository			cRepo;
	
	@Inject OptinMailRepo				optMailRepo;
	
	@Inject VersionedContentRepository  vcRepo;
	
	@Inject SimpleMailService			mailer;
	
	@Inject MailingPreProcessor			preProcessor;
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.publicapi.service.PublicAPIService#getPublicSubscriptionListByOrgUnit(org.bson.types.ObjectId, boolean)
	 */
	@Override
	public List<SubscriptionList> getPublicSubscriptionListByOrgUnit(
			ObjectId id, boolean deep) {
	
		if (!deep) {
			return mlRepo.findAllPublicByOwner(id);
		} else {
			OrgUnit root = orgUnitRepo.findOne(id);
			if (root == null) 
				return null;
			
			List<SubscriptionList> retval = new LinkedList<>();
			retval.addAll(mlRepo.findAllPublicByOwner(id));
			
			List<OrgUnit> children = orgUnitRepo.findByParent(root);
			while (children.size() > 0) {
				List<ObjectId> ids = new ArrayList<>();
				children.forEach(c -> ids.add(c.getId()));
				retval.addAll(mlRepo.findAllPublicByOwners(ids));
				
				// collect all childrens children
				List<OrgUnit> grandchildren = new LinkedList<>();
				children.forEach(c -> {
					grandchildren.addAll(orgUnitRepo.findByParent(c));
				});
				children = grandchildren;
			}
			return retval;
		}
	}
	
	/**
	 * Returns whether or not the provided contact holds the given email address
	 * @param c The contact to be testes
	 * @param email the email address
	 * @return True, if the contact hols that email address, false otherwise
	 */
	private boolean isAuthenticated(Contact c, String email) {
		Iterator<EMailAddress> emt = c.getEmailAddresses().iterator();
		while (emt.hasNext()) {
			EMailAddress e = emt.next();
			if (e.toString().equals(email)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Finds a given contact for a collection of email addresses
	 * @param addresses The collection of email addresses
	 * @return True, if a contact exists, that holds one of the given addresses, null otherwise
	 */
	private Contact findByEmail(Collection<EMailAddress> addresses) {
		Iterator<EMailAddress> eit = addresses.iterator();
		while (eit.hasNext()) {
			EMailAddress em = eit.next();
			Contact q = cRepo.findByEmailAddress(em.toString());
			if (q != null) 
				return q;
		}
		return null;
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.publicapi.service.PublicAPIService#getSubscriberById(org.bson.types.ObjectId, java.lang.String)
	 */
	@Override
	public PublicSubscriber getSubscriberById(ObjectId id, String email) throws EntityNotFoundException {
		Contact c = cRepo.findOne(id);
		if (c == null || !isAuthenticated(c, email)) {
			throw new EntityNotFoundException("The subscriber for id "+id+" can not be found");
		}
		PublicSubscriber retval = asDTO(c, PublicSubscriber.class);
		List<Subscription> subs = subRepo.findAllActiveByContact(c.getId());
		subs.forEach(s -> {
			if (s.getSubscriptionList().isPubliclyAvailable()) {
				retval.getSubscriptions().add(new PubSubscription(s.getSubscriptionList()));
			}
		});
		return retval;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.publicapi.service.PublicAPIService#createSubscriber(de.mfischbo.bustamail.publicapi.dto.PublicSubscriber, java.lang.String)
	 */
	@Override
	public PublicSubscriber createSubscriber(PublicSubscriber subscriber, String sourceIP) throws ApiException {
		Contact c = findByEmail(subscriber.getEmailAddresses()); 
		if (c == null) {
			c = asDTO(subscriber, Contact.class);
			c = cRepo.save(c);
		}
		
		EMailAddress primary = null;
		if (c.getEmailAddresses().size() > 0)
			primary = c.getEmailAddresses().iterator().next();
		
		final UUID transactionId = UUID.randomUUID();
		final List<SubscriptionList> lists = new ArrayList<>();
		final EMailAddress a = primary;
		final Contact x = c;
		PublicSubscriber retval = asDTO(c, PublicSubscriber.class);
		
		// check for a valid optin mail before creating subscriptions
		OptinMail mail = null;
		for (PubSubscription ps : subscriber.getSubscriptions()) {
			SubscriptionList slist = mlRepo.findOne(ps.getId());
			if (slist != null) {
				mail = optMailRepo.findActivatedByOwner(slist.getOwner());
				if (mail != null) break;
			}
		}
	
		if (mail == null)
			throw new ApiException("Unable to process subscription. No optin mail defined");
		
		// create a subscription for each PubSubscription
		subscriber.getSubscriptions().forEach(s -> {
			Subscription sub = new Subscription();
			sub.setContact(x);
			sub.setTransactionId(transactionId);
			sub.setDateCreated(DateTime.now());
			sub.setSourceType(SourceType.PublicAPIAction);
			sub.setState(State.OPTIN);
			sub.setIpAddress(sourceIP);
			sub.setEmailAddress(a);
			
			SubscriptionList slist = mlRepo.findOne(s.getId());
			if (slist != null) {
				sub.setSubscriptionList(slist);
				subRepo.save(sub);
				lists.add(slist);
				retval.getSubscriptions().add(new PubSubscription(slist));
			}
		});
		if (subscriber.getSubscriptions().size() > 0) {
			VersionedContent html = vcRepo.findByForeignId(mail.getId(), oneByDateCreatedDesc()).getContent().get(0);
			try {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("transactionId", transactionId);
				data.put("activationMode", mail.getActivationMode().toString());
				data.put("targetURL", mail.getTargetURL());
				data.put("subscriberId", x.getId());
				
				LiveMailing m = preProcessor.createLiveMailing(
						mail, Collections.singleton(c), html.getContent(), null, data);
				mailer.scheduleOptinMailing(m);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return retval;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.publicapi.service.PublicAPIService#updateSubscriber(de.mfischbo.bustamail.publicapi.dto.PublicSubscriber, java.lang.String)
	 */
	@Override
	public PublicSubscriber updateSubscriber(PublicSubscriber subscriber, String email) throws EntityNotFoundException {
		Contact c = cRepo.findOne(subscriber.getId());
		if (c == null || !isAuthenticated(c, email)) 
			throw new EntityNotFoundException("No contact found for id " + subscriber.getId());
		
		c = asDTO(subscriber, Contact.class);
		PublicSubscriber retval = asDTO(c, PublicSubscriber.class);
		retval.setSubscriptions(subscriber.getSubscriptions());
		return retval;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.publicapi.service.PublicAPIService#deleteSubscriber(de.mfischbo.bustamail.publicapi.dto.PublicSubscriber, java.lang.String)
	 */
	@Override
	public void deleteSubscriber(PublicSubscriber subscriber, String email) throws EntityNotFoundException {
		Contact c = cRepo.findOne(subscriber.getId());
		if (c == null || !isAuthenticated(c, email))
			throw new EntityNotFoundException("No contact found for id " + subscriber.getId());
		
		List<Subscription> subscriptions = subRepo.findAllActiveByContact(c.getId());
		subscriptions.forEach(s -> {
			s.setState(State.INACTIVE);
			s.setDateModified(DateTime.now());
		});
		subRepo.save(subscriptions);
		
		// hmm... maybe we should remove the contact as well or flag it deleted?
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.publicapi.service.PublicAPIService#createSubscriptions(de.mfischbo.bustamail.publicapi.dto.PublicSubscriber, java.lang.String, java.util.List, java.lang.String)
	 */
	@Override
	public PublicSubscriber createSubscriptions(PublicSubscriber subscriber, String email, List<ObjectId> listIds, String ipAddr) throws EntityNotFoundException {
		Contact c = cRepo.findOne(subscriber.getId());
		if (c == null || !isAuthenticated(c, email))
			throw new EntityNotFoundException("No contact found for id " + subscriber.getId());
		
		listIds.forEach(id -> {
			SubscriptionList slist = mlRepo.findOne(id);
			Subscription sub = subRepo.findBySubscriptionListAndContact(slist, c);
			if (sub == null) {
				Subscription s = new Subscription();
				s.setContact(c);
				s.setDateCreated(DateTime.now());
				s.setDateModified(DateTime.now());
				s.setEmailAddress(new EMailAddress(email));
				s.setIpAddress(ipAddr);
				s.setSourceType(SourceType.PublicAPIAction);
				s.setState(State.ACTIVE);
				s.setSubscriptionList(slist);
				subRepo.save(s);
				//subscriber.getSubscriptions().add(new PubSubscription(slist));
			}
		});
		return subscriber;
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.publicapi.service.PublicAPIService#deleteSubscriptions(de.mfischbo.bustamail.publicapi.dto.PublicSubscriber, java.lang.String, java.util.List)
	 */
	@Override
	public PublicSubscriber deleteSubscriptions(PublicSubscriber subscriber, String email, List<ObjectId> listIds) throws EntityNotFoundException {
		Contact c = cRepo.findOne(subscriber.getId());
		if (c == null || !isAuthenticated(c, email)) 
			throw new EntityNotFoundException("No contact found for id " + subscriber.getId());
		
		listIds.forEach(id -> {
			SubscriptionList slist = mlRepo.findOne(id);
			Subscription     sub   = subRepo.findBySubscriptionListAndContact(slist, c);
			if (sub != null) {
				sub.setState(State.INACTIVE);
				sub.setDateModified(DateTime.now());
				subRepo.save(sub);
			}
		});
		
		Iterator<PubSubscription> pit = subscriber.getSubscriptions().iterator();
		while (pit.hasNext()) {
			ObjectId pid = pit.next().getId();
			for (ObjectId id : listIds) {
				if (pid.equals(id))
					pit.remove();
			}
		}
		return subscriber;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.publicapi.service.PublicAPIService#activateSubscriptions(java.util.UUID)
	 */
	@Override
	public void activateSubscriptions(UUID transactionId) {
		List<Subscription> subs = subRepo.findByTransactionId(transactionId);
		subs.forEach(s -> {
			if (s.getState().equals(State.OPTIN)) {
				s.setDateModified(DateTime.now());
				s.setState(State.ACTIVE);
			}
		});
		subRepo.save(subs);
	}
}
