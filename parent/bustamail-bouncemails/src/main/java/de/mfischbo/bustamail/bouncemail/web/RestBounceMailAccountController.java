package de.mfischbo.bustamail.bouncemail.web;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.bouncemail.domain.BounceAccount;
import de.mfischbo.bustamail.bouncemail.service.BounceMailAccountService;
import de.mfischbo.bustamail.common.web.BaseApiController;

@RestController
@RequestMapping("/api/bounceaccounts")
public class RestBounceMailAccountController extends BaseApiController {

	@Inject
	private BounceMailAccountService	service;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Page<BounceAccount> getAllBounceMailAccounts(
			@RequestParam("owner") ObjectId ownerId,
			@PageableDefault(size = 30) Pageable page) throws Exception {
		return service.getAllAccounts(ownerId, page);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public BounceAccount getAccountById(@PathVariable("id") ObjectId id) throws Exception {
		return service.getAccountById(id);
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public BounceAccount createAccount(@RequestBody BounceAccount account) throws Exception {
		return service.createAccount(account);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public BounceAccount updateAccount(@PathVariable("id") ObjectId id, 
			@RequestBody BounceAccount account) throws Exception {
		account.setId(id);
		return service.updateAccout(account);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteAccount(@PathVariable("id") ObjectId id) throws Exception {
		BounceAccount account = service.getAccountById(id);
		service.deleteAccount(account);
	}
}
