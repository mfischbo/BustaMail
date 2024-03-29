package de.mfischbo.bustamail.mailinglist.service;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailinglist.domain.Subscription;
import de.mfischbo.bustamail.mailinglist.domain.Subscription.State;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.dto.ImportResultDTO;
import de.mfischbo.bustamail.mailinglist.dto.ParsingResultDTO;
import de.mfischbo.bustamail.mailinglist.dto.SubscriptionImportDTO;
import de.mfischbo.bustamail.mailinglist.dto.SubscriptionListDTO;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.security.domain.OrgUnit;

public interface MailingListService {

	// -------------------------------/
	//		Subscription Lists		 /
	// -----------------------------/
	
	@PreAuthorize("hasPermission(#owner, 'Security.IS_ACTOR_OF')")
	public Page<SubscriptionList> getAllMailingLists(@P("owner") OrgUnit owner, Pageable page);

	@PostAuthorize("hasPermission(returnObject.owner, 'Security.IS_ACTOR_OF')")
	public SubscriptionList			getSubscriptionListById(ObjectId id) throws EntityNotFoundException;
	
	@PostAuthorize("hasPermission(#owner, 'Security.IS_ACTOR_OF')")
	public Page<SubscriptionList> findSubscriptionLists(OrgUnit owner, String query, Pageable page);

	@PreAuthorize("hasPermission(#list.owner, 'MailingList.MANAGE_SUBSCRIPTION_LISTS')")
	public SubscriptionList			createSubscriptionList(SubscriptionListDTO list) throws EntityNotFoundException;
	
	@PreAuthorize("hasPermission(#list.owner, 'MailingList.MANAGE_SUBSCRIPTION_LISTS')")
	public SubscriptionList			updateSubscriptionList(SubscriptionListDTO list) throws EntityNotFoundException;

	@PreAuthorize("hasPermission(#list.owner, 'MailingList.MANAGE_SUBSCRIPTION_LISTS')")
	public void						deleteSubscriptionList(SubscriptionList list) throws EntityNotFoundException;

	/**
	 * Returns the amount of subscriptions on the given list for the given state
	 * @param list The mailing list
	 * @param state The subscription state
	 * @return The amount of subscriptions
	 */
	@PreAuthorize("hasPermission(#list.owner, 'MailingList.MANAGE_SUBSCRIPTION_LISTS')")
	public long						getSubscriptionCountByState(SubscriptionList list, State state);

	
	// ----------------------------/
	// 		Subscriptions		  /
	// --------------------------/
	
	@PreAuthorize("hasPermission(#list.owner, 'Security.IS_ACTOR_OF')")
	public Page<Subscription> getSubscriptionsByList(SubscriptionList list, Pageable page);
	
	@PostAuthorize("hasPermission(returnObject.subscriptionList.owner, 'Security.IS_ACTOR_OF')")
	public Subscription getSubscriptionById(ObjectId subscriptionId) throws EntityNotFoundException;
	
	@PostAuthorize("hasPermission(#list.owner, 'Security.IS_ACTOR_OF')")
	public Page<Subscription> findSubscriptions(SubscriptionList list, String query, Pageable page);
	
	@PostAuthorize("hasPermission(#list.owner, 'Security.IS_ACTOR_OF')")
	public void unsubscribeSubscription(SubscriptionList list, Subscription subscription);
	
	@PostAuthorize("hasPermission(#m.owner, 'Security.IS_ACTOR_OF')")
	public SubscriptionImportDTO getEstimatedFileSettings(Media m) throws Exception;
	
	@PostAuthorize("hasPermission(#list.owner, 'Security.IS_ACTOR_OF')")
	public ParsingResultDTO parseForErrors(SubscriptionList list, Media m, SubscriptionImportDTO dto) throws Exception;
	
	@PostAuthorize("hasPermission(#list.owner, 'Security.IS_ACTOR_OF')")
	public ParsingResultDTO parseImportFile(SubscriptionList list, Media media, SubscriptionImportDTO settings) throws Exception;

	@Transactional
	@PostAuthorize("hasPermission(#list.owner, 'Security.IS_ACTOR_OF')")
	public ImportResultDTO importSubscriptions(SubscriptionList list, Media media, SubscriptionImportDTO settings) throws Exception;
}
