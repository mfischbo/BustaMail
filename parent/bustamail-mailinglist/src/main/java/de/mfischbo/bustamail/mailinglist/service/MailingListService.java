package de.mfischbo.bustamail.mailinglist.service;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.dto.SubscriptionListDTO;
import de.mfischbo.bustamail.security.domain.OrgUnit;

public interface MailingListService {

	@PreAuthorize("hasPermission(#owner, 'Security.IS_ACTOR_OF')")
	public Page<SubscriptionList> getAllMailingLists(@P("owner") OrgUnit owner, Pageable page);

	@PostAuthorize("hasPermission(returnObject.owner, 'Security.IS_ACTOR_OF')")
	public SubscriptionList			getSubscriptionListById(UUID id) throws EntityNotFoundException;

	@Transactional
	@PreAuthorize("hasPermission(#list.owner, 'MailingList.MANAGE_SUBSCRIPTION_LISTS')")
	public SubscriptionList			createSubscriptionList(SubscriptionListDTO list) throws EntityNotFoundException;
	
	@Transactional
	@PreAuthorize("hasPermission(#list.owner, 'MailingList.MANAGE_SUBSCRIPTION_LISTS')")
	public SubscriptionList			updateSubscriptionList(SubscriptionListDTO list) throws EntityNotFoundException;

	@Transactional
	@PreAuthorize("hasPermission(#list.owner, 'MailingList.MANAGE_SUBSCRIPTION_LISTS')")
	public void						deleteSubscriptionList(SubscriptionList list) throws EntityNotFoundException;
}
