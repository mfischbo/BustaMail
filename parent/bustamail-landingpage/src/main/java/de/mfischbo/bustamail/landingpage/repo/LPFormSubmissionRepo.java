package de.mfischbo.bustamail.landingpage.repo;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.mfischbo.bustamail.landingpage.domain.LPFormSubmission;

public interface LPFormSubmissionRepo extends
		MongoRepository<LPFormSubmission, UUID> {

}
