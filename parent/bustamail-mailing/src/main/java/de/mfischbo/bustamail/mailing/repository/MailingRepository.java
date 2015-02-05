package de.mfischbo.bustamail.mailing.repository;

import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.security.repository.OwnerMongoRepository;

public interface MailingRepository extends OwnerMongoRepository<Mailing, ObjectId> {

	
	@Query(" { 'owner' : { $in : ?0 } } ")
	Page<Mailing> getByOwnership(Set<ObjectId> orgUnits, Pageable page);
}
