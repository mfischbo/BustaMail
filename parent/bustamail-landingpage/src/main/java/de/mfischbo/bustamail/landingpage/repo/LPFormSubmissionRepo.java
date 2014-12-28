package de.mfischbo.bustamail.landingpage.repo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import de.mfischbo.bustamail.landingpage.domain.LPFormSubmission;

public interface LPFormSubmissionRepo extends
		MongoRepository<LPFormSubmission, ObjectId> {

}
