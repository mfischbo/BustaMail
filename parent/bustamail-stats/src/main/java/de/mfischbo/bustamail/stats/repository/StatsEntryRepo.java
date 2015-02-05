package de.mfischbo.bustamail.stats.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import de.mfischbo.bustamail.stats.domain.StatsEntry;
import de.mfischbo.bustamail.stats.domain.StatsEntry.RecordType;

public interface StatsEntryRepo extends MongoRepository<StatsEntry, ObjectId> {

	@Query(" { 'mailingId' : ?0, 'type' : { $in : ['SENT_SUCCESS', 'SENT_FAILURE'] } } ")
	public Page<StatsEntry> getSendingStatusByMailingId(ObjectId mailingId, Pageable page);
	
	@Query(value = " { 'mailingId' : ?0, 'type' : ?1 } ", count = true)
	public Long countEntriesByMailingAndType(ObjectId mailingId, RecordType type);
	
	@Query(value = " { 'mailingId' : ?0, 'targetUrl' : ?1, 'type' : 'OPEN' } ", count = true)
	public Long countClicksByMailingAndTargetUrl(ObjectId mailingId, String targetUrl);
	
	List<StatsEntry> findDistinctByMailingIdAndType(ObjectId mailingId, String type);
}
