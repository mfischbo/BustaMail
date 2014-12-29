package de.mfischbo.bustamail.bouncemail.web;

import java.util.List;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.bouncemail.domain.BounceMail;
import de.mfischbo.bustamail.bouncemail.domain.BounceAccount;
import de.mfischbo.bustamail.bouncemail.service.BounceMailAccountService;
import de.mfischbo.bustamail.common.web.BaseApiController;

@RestController
@RequestMapping("/api/bounceaccounts/{bid}/mails")
public class RestBounceMailController extends BaseApiController {

	@Inject
	private BounceMailAccountService service;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Page<BounceMail> getAllBounceMails(@PathVariable("bid") ObjectId accountId,
			@PageableDefault(size=30) Pageable page) throws Exception {
	
		BounceAccount account = service.getAccountById(accountId);
		return service.getMailsByAccount(account, page);
	}
	
	
	@RequestMapping(value = "/{mid}", method = RequestMethod.GET)
	public BounceMail getBounceMailById(@PathVariable("bid") ObjectId accountId, 
			@PathVariable("mid") ObjectId mailId) throws Exception {
		BounceAccount account = service.getAccountById(accountId);
		return service.getMailById(account, mailId);
	}
	
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public void deleteBounceMails(@PathVariable("bid") ObjectId accountId,
			@RequestBody List<ObjectId> mailIds) throws Exception {
		BounceAccount account = service.getAccountById(accountId);
		service.deleteBounceMailsById(account, mailIds);
	}
}
