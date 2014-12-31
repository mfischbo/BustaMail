package de.mfischbo.bustamail.bouncemail.repo;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import de.mfischbo.bustamail.bouncemail.domain.BounceAccount;
import de.mfischbo.bustamail.bouncemail.domain.BounceMail;

public interface BounceMailRepo extends MongoRepository<BounceMail, ObjectId> {

	@Query(" { 'accountId' : ?0 } ")
	Page<BounceMail> findAllByAccount(BounceAccount account, Pageable page);
	
	@Query(" { 'accountId' : ?0, '_id' : ?1} ")
	BounceMail findOneByAccount(BounceAccount account, ObjectId id);
	
	@Query(" { 'messageId' : ?0 } ")
	BounceMail findByMessageId(String messageId);
}
