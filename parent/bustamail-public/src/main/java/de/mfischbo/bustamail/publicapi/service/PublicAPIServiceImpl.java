package de.mfischbo.bustamail.publicapi.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.mailinglist.domain.Subscription;
import de.mfischbo.bustamail.mailinglist.domain.Subscription.SourceType;
import de.mfischbo.bustamail.mailinglist.domain.Subscription.State;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.repository.SubscriptionListRepository;
import de.mfischbo.bustamail.mailinglist.repository.SubscriptionRepository;
import de.mfischbo.bustamail.publicapi.dto.PublicSubscription;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.repository.OrgUnitRepository;
import de.mfischbo.bustamail.subscriber.domain.Contact;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;
import de.mfischbo.bustamail.subscriber.repository.ContactRepository;

@Service
public class PublicAPIServiceImpl implements PublicAPIService {

	@Inject	SubscriptionListRepository 	mlRepo;
	
	@Inject OrgUnitRepository			orgUnitRepo;
	
	@Inject SubscriptionRepository		subRepo;
	
	@Inject ContactRepository			cRepo;
	
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

	@Override
	public PublicSubscription getSubscriptionById(ObjectId id, String email) {
	
		Subscription s = subRepo.findOne(id);
		boolean isValid = false;
		if (s.getContact() == null || s.getContact().getEmailAddresses() == null)
			return null;
		
		for (EMailAddress e : s.getContact().getEmailAddresses()) {
			if (e.toString().equalsIgnoreCase(email)) {
				isValid = true;
				break;
			}
		}
		
		if (!isValid) return null;
		
		PublicSubscription retval = new PublicSubscription();
		retval.setId(s.getId());
		retval.setContact(s.getContact());
	
		List<Subscription> subscriptions = subRepo.findAllActiveByContact(s.getContact().getId());
		List<SubscriptionList> slists = new ArrayList<>(subscriptions.size());
		subscriptions.stream()
			.filter(q -> q.getSubscriptionList().isPubliclyAvailable())
			.forEach(q -> slists.add(q.getSubscriptionList()));
		retval.setSubscriptions(slists);
		return retval;
	}

	@Override
	public PublicSubscription createSubscription(PublicSubscription subscription) {
	
		// load all subscription lists given
		List<ObjectId> listIds = new LinkedList<>();
		subscription.getSubscriptions().forEach(l -> listIds.add(l.getId()));
		List<SubscriptionList> lists = mlRepo.findAllPublicByIds(listIds);
		
		// check for each email address if a contact already exists
		Set<EMailAddress> addresses = subscription.getContact().getEmailAddresses();
		Iterator<EMailAddress> eit = addresses.iterator();
		Contact c = null;
		while (eit.hasNext()) {
			EMailAddress em = eit.next();
			c = cRepo.findByEmailAddress(em.toString());
			if (c != null) break;
		}

		// use the incoming address for subscription
		final EMailAddress primary = subscription.getContact().getEmailAddresses().iterator().next();
		Contact i = subscription.getContact();
		if (c != null) {
			// TODO: Update the contacts fields here. 
			// TODO: Decide which email to use for subscription
		}
		
		c = cRepo.save(subscription.getContact());
		final List<Subscription> subs = new LinkedList<>();
		lists.forEach(l -> {
			Subscription s = new Subscription();
			//s.setContact(c);
			s.setDateCreated(DateTime.now());
			s.setEmailAddress(primary);
			s.setSourceType(SourceType.PublicAPIAction);
			s.setState(State.OPTIN);
			s.setSubscriptionList(l);
			subs.add(s);
		});
		List<Subscription> subs2 = subRepo.save(subs);
		
		PublicSubscription retval = new PublicSubscription();
		retval.setContact(c);
		retval.setId(subs2.get(0).getId());
		retval.setSubscriptions(lists);
		return retval;
	}

	@Override
	public PublicSubscription updateSubscription(PublicSubscription subscription) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PublicSubscription activateSubscription(
			PublicSubscription subscription) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteSubscripton(PublicSubscription subscription) {
		// TODO Auto-generated method stub

	}

}
