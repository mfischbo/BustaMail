package de.mfischbo.bustamail.landingpage.repo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.security.repository.OwnerMongoRepository;

public interface LandingPageRepo extends OwnerMongoRepository<LandingPage, ObjectId> {

	@Query(" { 'staticPages._id' : ?0 } ")
	public LandingPage findPageContainingStaticPageById(ObjectId staticPageId);
	
	@Query(" { 'forms._id' : ?0 } ")
	public LandingPage findPageContainingFormById(ObjectId id);
}
