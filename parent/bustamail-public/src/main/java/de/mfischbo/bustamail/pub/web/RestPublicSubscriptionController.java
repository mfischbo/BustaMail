package de.mfischbo.bustamail.pub.web;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.exception.BustaMailException;
import de.mfischbo.bustamail.mailinglist.domain.Subscription;
import de.mfischbo.bustamail.mailinglist.domain.Subscription.State;
import de.mfischbo.bustamail.mailinglist.repository.SubscriptionRepository;
import de.mfischbo.bustamail.pub.dto.PublicSubscriberDTO;
import de.mfischbo.bustamail.subscriber.domain.Contact;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;
import de.mfischbo.bustamail.subscriber.repository.ContactRepository;

@RestController
@RequestMapping("/api/public/subscribers")
public class RestPublicSubscriptionController {

	@Inject
	private ContactRepository cRepo;

	@Inject
	private SubscriptionRepository subRepo;
	
	@RequestMapping(value = "/{oid}/{cid}", method = RequestMethod.GET)
	public PublicSubscriberDTO getSubscriptionsByContact(@PathVariable("oid") ObjectId orgUnitId,
			@PathVariable("cid") ObjectId contactId, 
			@RequestParam("auth") String email) throws Exception {

		// load the contact and see if the email matches
		PublicSubscriberDTO retval = new PublicSubscriberDTO();
		Contact c = cRepo.findOne(contactId);
		if (c == null) 
			throw new BustaMailException("Unable to find a contact for the given id");
		
		boolean isAuthenticated = false;
		for (EMailAddress em : c.getEmailAddresses()) {
			if (em.toString().equalsIgnoreCase(email))
				isAuthenticated = true;
		}
		
		if (!isAuthenticated)
			throw new BustaMailException("The id and authentication token do not match");
		
		retval.setContact(c);
		
		List<Subscription> subscriptions = subRepo.findAllByContactId(contactId);
		retval.setSubscriptionLists(new ArrayList<>());
		for (Subscription s : subscriptions) {
			if (s.getSubscriptionList().getOwner().equals(orgUnitId) && 
					(s.getState() == State.ACTIVE || s.getState() == State.OPTIN )) 
				retval.getSubscriptionLists().add(s.getSubscriptionList());
		}
	
		if (retval.getSubscriptionLists().size() == 0)
			throw new BustaMailException("No such subscriber");
		
		return retval;
	}
}