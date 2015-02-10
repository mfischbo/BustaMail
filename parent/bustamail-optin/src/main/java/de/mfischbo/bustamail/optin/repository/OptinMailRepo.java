package de.mfischbo.bustamail.optin.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import de.mfischbo.bustamail.optin.domain.OptinMail;
import de.mfischbo.bustamail.security.repository.OwnerMongoRepository;

public interface OptinMailRepo extends OwnerMongoRepository<OptinMail, ObjectId> {

	@Query(" { 'activated' : true } ")
	List<OptinMail> findAllActivated();
}
