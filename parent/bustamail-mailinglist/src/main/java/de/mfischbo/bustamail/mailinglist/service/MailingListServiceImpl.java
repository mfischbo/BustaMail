package de.mfischbo.bustamail.mailinglist.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailinglist.domain.Subscription;
import de.mfischbo.bustamail.mailinglist.domain.Subscription.SourceType;
import de.mfischbo.bustamail.mailinglist.domain.Subscription.State;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.dto.ImportResultDTO;
import de.mfischbo.bustamail.mailinglist.dto.ParsingResultDTO;
import de.mfischbo.bustamail.mailinglist.dto.SubscriptionImportDTO;
import de.mfischbo.bustamail.mailinglist.dto.SubscriptionListDTO;
import de.mfischbo.bustamail.mailinglist.repository.SubscriptionListRepository;
import de.mfischbo.bustamail.mailinglist.repository.SubscriptionRepository;
import de.mfischbo.bustamail.mailinglist.validation.ImportValidator;
import de.mfischbo.bustamail.mailinglist.validation.Validator.ResultType;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.media.service.MediaService;
import de.mfischbo.bustamail.reader.IndexedPropertyHolder;
import de.mfischbo.bustamail.reader.TableDataReader;
import de.mfischbo.bustamail.security.domain.OrgUnit;
import de.mfischbo.bustamail.security.service.SecurityService;
import de.mfischbo.bustamail.subscriber.domain.Address;
import de.mfischbo.bustamail.subscriber.domain.Contact;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;
import de.mfischbo.bustamail.subscriber.service.SubscriberService;

@Service
public class MailingListServiceImpl extends BaseService implements MailingListService {

	@Inject
	SecurityService					secService;
	
	@Inject
	SubscriberService				subService;

	@Inject
	MediaService					mediaService;
	
	@Inject
	SubscriptionListRepository		sListRepo;
	
	@Inject
	SubscriptionRepository			scRepo;
	
	
	Map<ObjectId, ParsedImport>		importCache;
	
	/**
	 * Default constructor
	 */
	public MailingListServiceImpl() {
		this.importCache = new HashMap<ObjectId, ParsedImport>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailinglist.service.MailingListService#getAllMailingLists(de.mfischbo.bustamail.security.domain.OrgUnit, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<SubscriptionList> getAllMailingLists(OrgUnit owner,
			Pageable page) {
		return sListRepo.findAllByOwner(owner.getId(), page);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailinglist.service.MailingListService#getSubscriptionListById(java.util.UUID)
	 */
	@Override
	public SubscriptionList getSubscriptionListById(ObjectId id)
			throws EntityNotFoundException {
		SubscriptionList list = sListRepo.findOne(id);
		checkOnNull(list);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailinglist.service.MailingListService#findSubscriptionLists(de.mfischbo.bustamail.security.domain.OrgUnit, java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<SubscriptionList> findSubscriptionLists(OrgUnit owner, String query, Pageable page) {
		return sListRepo.findByOwnerAndNameLike(owner, query, page);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailinglist.service.MailingListService#createSubscriptionList(de.mfischbo.bustamail.mailinglist.dto.SubscriptionListDTO)
	 */
	@Override
	public SubscriptionList createSubscriptionList(SubscriptionListDTO list) throws EntityNotFoundException {
		SubscriptionList l = new SubscriptionList();
		l.setName(list.getName());
		l.setDescription(list.getDescription());
		
		OrgUnit owner = secService.getOrgUnitById(list.getOwner());
		checkOnNull(owner);
		l.setOwner(owner.getId());
		return sListRepo.save(l);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailinglist.service.MailingListService#updateSubscriptionList(de.mfischbo.bustamail.mailinglist.dto.SubscriptionListDTO)
	 */
	@Override
	public SubscriptionList updateSubscriptionList(SubscriptionListDTO list) throws EntityNotFoundException {
		SubscriptionList l = sListRepo.findOne(list.getId());
		checkOnNull(l);
		l.setName(list.getName());
		l.setDescription(list.getDescription());
		l.setPubliclyAvailable(list.isPubliclyAvailable());
		return sListRepo.save(l);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailinglist.service.MailingListService#deleteSubscriptionList(de.mfischbo.bustamail.mailinglist.domain.SubscriptionList)
	 */
	@Override
	public void deleteSubscriptionList(SubscriptionList list) throws EntityNotFoundException {
		sListRepo.delete(list);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailinglist.service.MailingListService#getSubscriptionCountByState(de.mfischbo.bustamail.mailinglist.domain.SubscriptionList, de.mfischbo.bustamail.mailinglist.domain.Subscription.State)
	 */
	@Override
	public long getSubscriptionCountByState(SubscriptionList list, State state) {
		return scRepo.countBySubscriptionListAndState(list, state);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailinglist.service.MailingListService#getSubscriptionsByList(de.mfischbo.bustamail.mailinglist.domain.SubscriptionList, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<Subscription> getSubscriptionsByList(SubscriptionList list,
			Pageable page) {
		return scRepo.findAllBySubscriptionList(list, page);
	}


	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailinglist.service.MailingListService#getSubscriptionById(java.util.UUID)
	 */
	@Override
	public Subscription getSubscriptionById(ObjectId subscriptionId)
			throws EntityNotFoundException {
		Subscription sub = scRepo.findOne(subscriptionId);
		checkOnNull(sub);
		return sub;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailinglist.service.MailingListService#findSubscriptions(de.mfischbo.bustamail.mailinglist.domain.SubscriptionList, java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<Subscription> findSubscriptions(SubscriptionList list, String query, Pageable page) {
	
		return new PageImpl<Subscription>(new ArrayList<Subscription>());
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mfischbo.bustamail.mailinglist.service.MailingListService#unsubscribeSubscription(de.mfischbo.bustamail.mailinglist.domain.SubscriptionList, de.mfischbo.bustamail.mailinglist.domain.Subscription)
	 */
	@Override
	public void unsubscribeSubscription(SubscriptionList list, Subscription subscription) {
		subscription.setState(State.INACTIVE);
		scRepo.save(subscription);
	}
	

	@Override
	public SubscriptionImportDTO getEstimatedFileSettings(Media m) throws Exception {
		SubscriptionImportDTO retval = new SubscriptionImportDTO();
		retval.setMediaId(m.getId());
		
		TableDataReader reader = new TableDataReader(m.asStream(), m.getMimetype(), m.getName());
		retval.setCsvQuoteChar(reader.getEstimatedQuoteChar());
		retval.setCsvDelimiter(reader.getEstimatedDelimiterChar());
		retval.setType(reader.getType());
		
		return retval;
	}
	

	@Override
	public ParsingResultDTO parseImportFile(SubscriptionList list, Media media, SubscriptionImportDTO settings)
			throws Exception {
		List<List<String>> data = getImportDataSecure(media, settings);
		ParsingResultDTO retval = new ParsingResultDTO();
		retval.setData(data);
		return retval;
	}

	
	@Override
	public ParsingResultDTO parseForErrors(SubscriptionList list, Media m,
			SubscriptionImportDTO dto) throws Exception {
	
		List<List<String>> data = getImportDataSecure(m, dto);//null;
	
		List<List<ResultType>> validationResults = new ArrayList<>(data.size());
		ImportValidator ival = new ImportValidator(dto.getFieldNames());
		data.stream().forEach(new Consumer<List<String>>() {
			@Override
			public void accept(List<String> t) {
				validationResults.add(ival.validate(t));
			}
		});
		
		ParsingResultDTO result = new ParsingResultDTO();
		result.setParsingResults(validationResults);
		
		// mark the lines that contain errors
		for (int i=0; i < validationResults.size(); i++) {
			long warnings = validationResults.get(i).stream().filter(q -> q != ResultType.SUCCESS).count();
			if (warnings > 0) {
				if (!(i == 0 && dto.isContainsHeader()))
					result.getErrorLines().add(i);
			}
		}
		return result;
	}

	@Override
	public ImportResultDTO importSubscriptions(SubscriptionList list,
			Media media, SubscriptionImportDTO settings) throws Exception {

		List<Contact> contacts = mapDataToContacts(media, settings);
		
		// stats to be returned
		ImportResultDTO	retval = new ImportResultDTO();
		int imports = 0;
		
	
		for (Contact c : contacts) {
			EMailAddress lookup = c.getEmailAddresses().iterator().next();
			c = getPersistedContact(c, settings, retval);
			
			// contact is now persisted and stored in c. Find the persisted email address to use for lookup
			for (EMailAddress e : c.getEmailAddresses()) {
				if (e.equals(lookup)) {
					lookup = e;
					break;
				}
			}

			Subscription s = scRepo.findBySubscriptionListAndContact(list, c);
			if (s == null) {
				s = new Subscription();
				s.setDateCreated(DateTime.now());
				s.setEmailAddress(lookup);
				s.setIpAddress("127.0.0.1");
				s.setSourceType(SourceType.ImportAction);
				s.setState(State.ACTIVE);
				s.setSubscriptionList(list);
				s.setContact(c);
				scRepo.save(s);
				imports++;
			}
		}
		
		// remove the data source from the cache
		if (importCache.containsKey(media.getId()))
			importCache.remove(media.getId());
		
		retval.setLinesImported(imports);
		return retval;
	}

	/**
	 * Maps raw table data to a list of contacts using a dozer mapper
	 * @param media The import file used for reading data either from the file itself or from cache
	 * @param settings The import settings
	 * @return A list of contacts
	 */
	private List<Contact> mapDataToContacts(Media media, SubscriptionImportDTO settings) {

		List<List<String>> data = getImportDataSecure(media, settings);
		if (settings.isContainsHeader())
			data.remove(0);
			
		log.debug("Mapping all data rows to contacts...");
		DozerBeanMapper dm = DozerImportFactory.createInstance(Contact.class, settings);
		List<Contact> contacts = new ArrayList<>();
		for (List<String> l : data) {
			IndexedPropertyHolder holder = new IndexedPropertyHolder();
			holder.getColumns().addAll(l);
			Contact c = new Contact();
			dm.map(holder, c);
			contacts.add(c);
		}
		log.debug("Mapped ["+contacts.size()+"] rows to contacts.");
		return contacts;
	}


	/**
	 * Returns a persisted entity of the contact. The method ensures that all related entities are persisted as well.
	 * If the override flag is set in the settings, the contact will be updated.
	 * @param c The contact to be persisted
	 * @param settings The settings
	 * @return The persisted contact
	 */
	private Contact getPersistedContact(Contact c, SubscriptionImportDTO settings, ImportResultDTO result) {
	
		EMailAddress lookup = c.getEmailAddresses().iterator().next();
		Contact pers = null;
		try {
			pers = subService.getContactByEMailAddress(lookup);
		} catch (EntityNotFoundException ex) {
			log.error(ex.getMessage());
		}
		
		if (pers != null) {
			if (settings.isOverride()) {
				pers.setFirstName(ImportUtility.bestMatchingResult(pers.getFirstName(), c.getFirstName()));
				pers.setFormalSalutation(c.isFormalSalutation());
				pers.setGender(ImportUtility.bestMatchingResult(pers.getGender(), c.getGender()));
				pers.setLastName(ImportUtility.bestMatchingResult(pers.getFirstName(), c.getFirstName()));
				pers.setTitle(ImportUtility.bestMatchingResult(pers.getTitle(), c.getTitle()));
			
				// add the new address if not available yet. Comparisson based on Address#equals() implementation!
				if (c.getAddresses() != null && c.getAddresses().size() > 0) {
					Address impAddr = c.getAddresses().get(0);
					if (impAddr != null) {
						if (!pers.getAddresses().contains(impAddr)) {
							pers.getAddresses().add(impAddr);
							//impAddr.setContact(pers);
						}
					}
				}
				
				// add the new email address if not available yet. Comparisson based on EMailAddress#equals() implementation!
				if (!pers.getEmailAddresses().contains(lookup)) {
					pers.getEmailAddresses().add(lookup);
					//lookup.setContact(pers);
				} 
				
				try {
					c = subService.updateContact(pers);
					result.setContactsUpdated(result.getContactsUpdated() + 1);
				} catch (Exception ex) {
					ex.printStackTrace();
					log.error(ex.getMessage());
				}
			} else {
				c = pers;
			}
		}
	
		// case contact not found -> create a new one
		if (pers == null) {
			//lookup.setContact(c);
		
			/*
			if (c.getAddresses() != null && c.getAddresses().size() > 0)
				c.getAddresses().get(0).setContact(c);
			*/
			
			c = subService.createContact(c);
			result.setContactsCreated(result.getContactsCreated() + 1);
		}
		return c;
	}
	

	/**
	 * Returns the import data from either the given media file or from the import cache
	 * @param media The media file containing the data to be imported
	 * @param settings The settings for import
	 * @return The raw table data as returned from the reader
	 */
	private List<List<String>> getImportDataSecure(Media media, SubscriptionImportDTO settings) {
		List<List<String>> data = null;
		
		// check cache for the data
		if (importCache.containsKey(media.getId())) {
			ParsedImport pi = importCache.get(media.getId());
			if (pi.settings.equals(settings))
				data = pi.data;
		}
	
		// if cache results in null reparse
		if (data == null) {
			try {
				// ensure to use a buffered stream
				InputStream stream = mediaService.getContent(media);
				ByteArrayInputStream bStream = new ByteArrayInputStream(StreamUtils.copyToByteArray(stream));
				
				TableDataReader reader = new TableDataReader(bStream, media.getMimetype(), media.getName());
				reader.setCsvDelimiterChar(settings.getCsvDelimiter());
				reader.setCsvQuoteChar(settings.getCsvQuoteChar());
				reader.setReaderEncoding(Charset.forName(settings.getEncoding().toString()));
				data = reader.getRawTableData();
				
				ParsedImport pi = new ParsedImport();
				pi.data = data;
				pi.settings = settings;
				importCache.put(media.getId(), pi);
			} catch (Exception ex) {
				throw new RuntimeException("Could not retrieve data from media file : " + media.getId());
			}
		}
		Assert.notNull(data, "Could not retrieve import data. No readable data in cache nor in file.");
		return data;
	}
}