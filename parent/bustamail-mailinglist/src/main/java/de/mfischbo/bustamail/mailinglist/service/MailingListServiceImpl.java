package de.mfischbo.bustamail.mailinglist.service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.exception.EntityNotFoundException;
import de.mfischbo.bustamail.mailinglist.domain.Subscription;
import de.mfischbo.bustamail.mailinglist.domain.SubscriptionList;
import de.mfischbo.bustamail.mailinglist.dto.ParsingResultDTO;
import de.mfischbo.bustamail.mailinglist.dto.SubscriptionImportDTO;
import de.mfischbo.bustamail.mailinglist.dto.SubscriptionListDTO;
import de.mfischbo.bustamail.mailinglist.repository.SubscriptionListRepository;
import de.mfischbo.bustamail.mailinglist.repository.SubscriptionRepository;
import de.mfischbo.bustamail.mailinglist.validation.ImportValidator;
import de.mfischbo.bustamail.mailinglist.validation.Validator.ResultType;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.reader.TableDataReader;
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
	
	
	Map<UUID, ParsedImport>			importCache;
	
	/**
	 * Default constructor
	 */
	public MailingListServiceImpl() {
		MailingListPermissionProvider mlpp = new MailingListPermissionProvider();
		PermissionRegistry.registerPermissions(mlpp.getModulePermissions());
		
		this.importCache = new HashMap<UUID, ParsedImport>();
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
	public SubscriptionList getSubscriptionListById(UUID id)
			throws EntityNotFoundException {
		SubscriptionList list = sListRepo.findOne(id);
		checkOnNull(list);
		return list;
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
		return sListRepo.saveAndFlush(l);
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
		return sListRepo.saveAndFlush(l);
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
	public Subscription getSubscriptionById(UUID subscriptionId)
			throws EntityNotFoundException {
		Subscription sub = scRepo.findOne(subscriptionId);
		checkOnNull(sub);
		return sub;
	}

	@Override
	public SubscriptionImportDTO getEstimatedFileSettings(Media m) throws Exception {
		SubscriptionImportDTO retval = new SubscriptionImportDTO();
		retval.setMediaId(m.getId());
		
		TableDataReader reader = new TableDataReader(m.getDataStream(), m.getMimetype(), m.getName());
		retval.setCsvQuoteChar(reader.getEstimatedQuoteChar());
		retval.setCsvDelimiter(reader.getEstimatedDelimiterChar());
		retval.setType(reader.getType());
		
		return retval;
	}
	

	@Override
	public ParsingResultDTO parseImportFile(SubscriptionList list, Media media, SubscriptionImportDTO settings)
			throws Exception {
		
		List<List<String>> data = null;
		
		// check cache for the data
		if (importCache.containsKey(media.getId())) {
			ParsedImport pi = importCache.get(media.getId());
			if (pi.settings.equals(settings))
				data = pi.data;
		}
	
		// if cache results in null reparse
		if (data == null) {
			TableDataReader reader = new TableDataReader(media.getDataStream(), media.getMimetype(), media.getName());
			reader.setCsvDelimiterChar(settings.getCsvDelimiter());
			reader.setCsvQuoteChar(settings.getCsvQuoteChar());
			reader.setReaderEncoding(Charset.forName(settings.getEncoding().toString()));
			data = reader.getRawTableData();
			
			ParsedImport pi = new ParsedImport();
			pi.data = data;
			pi.settings = settings;
			importCache.put(media.getId(), pi);
		}
		
		ParsingResultDTO retval = new ParsingResultDTO();
		retval.setData(data);
		return retval;
	}

	@Override
	public ParsingResultDTO parseForErrors(SubscriptionList list, Media m,
			SubscriptionImportDTO dto) throws Exception {
	
		List<List<String>> data = null;
		if (importCache.containsKey(m.getId())) {
			ParsedImport pi = importCache.get(m.getId());
			if (pi.settings.equals(dto)) 
				data = pi.data;
		}
		
		if (data == null) {
			TableDataReader reader = new TableDataReader(m.getDataStream(), m.getMimetype(), m.getName());
			reader.setCsvDelimiterChar(dto.getCsvDelimiter());
			reader.setCsvQuoteChar(dto.getCsvQuoteChar());
			reader.setReaderEncoding(Charset.forName(dto.getEncoding()));
			data = reader.getRawTableData();
		}
		
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
		/*
		BeanMappingBuilder b = new BeanMappingBuilder() {
		
			@Override
			protected void configure() {
				final TypeMappingBuilder tmb = mapping(IndexedPropertyHolder.class, Contact.class, oneWay(), wildcard(false), mapNull(true));
				for (int i=0; i < dto.getFieldNames().length; i++) {
					
					String field = dto.getFieldNames()[i];
					if (field.trim().length() == 0)
						continue;
					
					tmb.fields("columns[" + i + "]", field);
				}
			}
		};
		
		DozerBeanMapper dm = new DozerBeanMapper();
		dm.addMapping(b);
	
	
		List<Contact> contacts = new ArrayList<>();
		for (List<String> l : data) {
			IndexedPropertyHolder holder = new IndexedPropertyHolder();
			holder.getColumns().addAll(l);
			Contact c = new Contact();
			dm.map(holder, c);
			contacts.add(c);
		}
		*/
	}
}