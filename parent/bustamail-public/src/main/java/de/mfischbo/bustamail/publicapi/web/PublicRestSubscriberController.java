package de.mfischbo.bustamail.publicapi.web;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.exception.ApiException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.publicapi.dto.PublicSubscriber;
import de.mfischbo.bustamail.publicapi.service.PublicAPIService;

@RestController
@RequestMapping(value = "/api/public/subscriber")
public class PublicRestSubscriberController {

	@Inject PublicAPIService		service;
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public PublicSubscriber getSubscriberById(@PathVariable("id") ObjectId id,
			@RequestParam(value = "email", required = true) String email) throws EntityNotFoundException {
		return service.getSubscriberById(id, email);
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public PublicSubscriber createSubscriber(@RequestBody PublicSubscriber subscriber,
			HttpServletRequest request) throws ApiException {
		return service.createSubscriber(subscriber, request.getRemoteAddr());
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public PublicSubscriber updateSubscriber(@RequestBody PublicSubscriber subscriber,
			@RequestParam(value = "email", required = true) String email) throws EntityNotFoundException {
		return service.updateSubscriber(subscriber, email);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteSubscriber(@PathVariable("id") ObjectId id, 
			@RequestParam(value = "email", required = true) String email) throws EntityNotFoundException {
		PublicSubscriber sub = service.getSubscriberById(id, email);
		service.deleteSubscriber(sub, email);
	}

	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.PUT)
	public PublicSubscriber createSubscription(@PathVariable("id") ObjectId id,
			@RequestParam(value = "email", required = true) String email,
			@RequestBody List<ObjectId> listIds,
			HttpServletRequest request) throws EntityNotFoundException {
		PublicSubscriber sub = service.getSubscriberById(id, email);
		return service.createSubscriptions(sub, email, listIds, request.getRemoteAddr());
	}
	
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.DELETE)
	public PublicSubscriber deleteSubscriptions(@PathVariable("id") ObjectId id,
			@RequestParam(value = "email", required = true) String email,
			@RequestBody List<ObjectId> listIds) throws EntityNotFoundException {
		PublicSubscriber sub = service.getSubscriberById(id, email);
		return service.deleteSubscriptions(sub, email, listIds);
	}
}
