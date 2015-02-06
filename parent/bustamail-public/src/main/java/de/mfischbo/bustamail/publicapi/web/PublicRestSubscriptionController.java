package de.mfischbo.bustamail.publicapi.web;

import javax.inject.Inject;
import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.publicapi.dto.PublicSubscription;
import de.mfischbo.bustamail.publicapi.service.PublicAPIService;

@RestController
@RequestMapping(value = "/api/public/subscriptions")
public class PublicRestSubscriptionController extends BaseApiController {

	@Inject private PublicAPIService		service;
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public PublicSubscription getSubscriptionById(@PathVariable("id") ObjectId subscriptionId,
			@RequestParam(value = "e", required=true) String email) {
		return service.getSubscriptionById(subscriptionId, email);
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public PublicSubscription createSubscription(@Valid @RequestBody PublicSubscription subscription) {
		return service.createSubscription(subscription);
	}
	
	@RequestMapping(value = "/{id}/optin", method = RequestMethod.PUT)
	public void activateSubscription(@PathVariable("id") ObjectId subscriptionId,
			@RequestParam(value = "e", required = true) String email) {
		PublicSubscription s = service.getSubscriptionById(subscriptionId, email);
		service.activateSubscription(s);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public void updateSubscription(@PathVariable("id") ObjectId subscriptionId,
			@RequestParam(value = "e", required = true) String email,
			@RequestBody PublicSubscription subscription) {
		service.updateSubscription(subscription);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteSubscription(@PathVariable("id") ObjectId subscriptionId,
			@RequestParam(value = "e", required = true) String email) {
		PublicSubscription s = service.getSubscriptionById(subscriptionId, email);
		service.deleteSubscripton(s);
	}
}
