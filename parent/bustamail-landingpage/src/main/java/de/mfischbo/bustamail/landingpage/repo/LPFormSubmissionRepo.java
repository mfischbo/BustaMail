package de.mfischbo.bustamail.landingpage.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mfischbo.bustamail.landingpage.domain.LPFormSubmission;

public interface LPFormSubmissionRepo extends
		JpaRepository<LPFormSubmission, UUID> {

}
