package de.mfischbo.bustamail.subscriber.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import de.mfischbo.bustamail.subscriber.domain.Contact;

public interface ContactRepository extends MongoRepository<Contact, ObjectId> {

	@Query(" { 'emailAddresses.address' : ?0 } ")
	Contact findByEmailAddress(String address);
}
