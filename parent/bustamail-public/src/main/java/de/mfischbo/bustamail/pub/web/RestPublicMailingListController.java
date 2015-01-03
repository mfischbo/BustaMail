package de.mfischbo.bustamail.pub.web;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.repository.SubscriptionListRepository;

@RestController
@RequestMapping("/api/public/mailing-lists")
public class RestPublicMailingListController {

	@Inject
	private SubscriptionListRepository repo;
	
	@RequestMapping(value = "/{oid}", method = RequestMethod.GET)
	public Page<SubscriptionList> getAllPublicMailingLists(@PathVariable("oid") ObjectId orgUnit, @PageableDefault(size=30) Pageable page) throws Exception {
		return repo.findAllPublicByOwner(orgUnit, page);
	}
}
