package de.mfischbo.bustamail.bouncemail.repo;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import de.mfischbo.bustamail.bouncemail.domain.BounceAccount;
import de.mfischbo.bustamail.security.repository.OwnerMongoRepository;

public interface BounceAccountRepo extends OwnerMongoRepository<BounceAccount, ObjectId>{

	@Query(" { 'enabled' : true } ")
	public List<BounceAccount> findAllEnabled();
}
