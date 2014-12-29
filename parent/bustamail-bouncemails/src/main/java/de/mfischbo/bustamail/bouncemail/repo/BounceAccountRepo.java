package de.mfischbo.bustamail.bouncemail.repo;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import de.mfischbo.bustamail.bouncemail.domain.BounceAccount;

public interface BounceAccountRepo extends MongoRepository<BounceAccount, ObjectId>{

	@Query(" { 'enabled' : true } ")
	public List<BounceAccount> findAllEnabled();
}
