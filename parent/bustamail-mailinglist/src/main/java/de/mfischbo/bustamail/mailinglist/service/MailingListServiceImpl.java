package de.mfischbo.bustamail.mailinglist.service;

import java.util.UUID;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailinglist.domain.Subscription;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.dto.SubscriptionListDTO;
import de.mfischbo.bustamail.mailinglist.repository.SubscriptionListRepository;
import de.mfischbo.bustamail.mailinglist.repository.SubscriptionRepository;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.service.PermissionRegistry;
import de.mfischbo.bustamail.security.service.SecurityService;

@Service
public class MailingListServiceImpl extends BaseService implements MailingListService {

	@Inject
	SecurityService					secService;
	
	@Inject
	SubscriptionListRepository		sListRepo;
	
	@Inject
	SubscriptionRepository			scRepo;
	
	
	public MailingListServiceImpl() {
		MailingListPermissionProvider mlpp = new MailingListPermissionProvider();
		PermissionRegistry.registerPermissions(mlpp.getModulePermissions());
	}
	
	
	@Override
	public Page<SubscriptionList> getAllMailingLists(OrgUnit owner,
			Pageable page) {
		return sListRepo.findAllByOwner(owner.getId(), page);
	}

	@Override
	public SubscriptionList getSubscriptionListById(UUID id)
			throws EntityNotFoundException {
		SubscriptionList list = sListRepo.findOne(id);
		checkOnNull(list);
		return list;
	}

	@Override
	public SubscriptionList createSubscriptionList(SubscriptionListDTO list) throws EntityNotFoundException {
		SubscriptionList l = new SubscriptionList();
		l.setName(list.getName());
		l.setDescription(list.getDescription());
		
		OrgUnit owner = secService.getOrgUnitById(list.getOwner());
		checkOnNull(owner);
		l.setOwner(owner.getId());
		return sListRepo.saveAndFlush(l);
	}

	@Override
	public SubscriptionList updateSubscriptionList(SubscriptionListDTO list) throws EntityNotFoundException {
		SubscriptionList l = sListRepo.findOne(list.getId());
		checkOnNull(l);
		l.setName(list.getName());
		l.setDescription(list.getDescription());
		return sListRepo.saveAndFlush(l);
	}

	@Override
	public void deleteSubscriptionList(SubscriptionList list) throws EntityNotFoundException {
		sListRepo.delete(list);
	}


	@Override
	public Page<Subscription> getSubscriptionsByList(SubscriptionList list,
			Pageable page) {
		return scRepo.findAllBySubscriptionList(list, page);
	}


	@Override
	public Subscription getSubscriptionById(UUID subscriptionId)
			throws EntityNotFoundException {
		Subscription sub = scRepo.findOne(subscriptionId);
		checkOnNull(sub);
		return sub;
	}
}