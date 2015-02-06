package de.mfischbo.bustamail.publicapi.web;

import java.util.List;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.common.web.BaseApiController;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.publicapi.service.PublicAPIService;

@RestController
@RequestMapping(value = "/api/public/{unit}/subscription-lists", produces = "application/json")
public class PublicRestSubscriptionListController extends BaseApiController {

	@Inject private PublicAPIService		service;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<SubscriptionList> getAllSubscriptionLists(@PathVariable("unit") ObjectId orgUnitId,
			@RequestParam(value = "deep", required = false, defaultValue = "false") boolean deep) {
		
		return service.getPublicSubscriptionListByOrgUnit(orgUnitId, deep);
	}
}