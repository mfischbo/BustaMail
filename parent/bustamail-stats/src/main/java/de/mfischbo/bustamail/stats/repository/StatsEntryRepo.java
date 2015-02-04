package de.mfischbo.bustamail.stats.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import de.mfischbo.bustamail.stats.domain.StatsEntry;

public interface StatsEntryRepo extends MongoRepository<StatsEntry, ObjectId> {

	@Query(" { 'mailingId' : ?0, 'type' : 'OPEN' } ")
	public Long getOpeningsByMailingId(ObjectId mailingId);
	
	@Query(" { 'mailingId' : ?0, 'type' : 'OPEN'} ")
	public Long getUniqueOpeningsByMailing(ObjectId mailingId);
}
