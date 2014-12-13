package de.mfischbo.bustamail.landingpage.repo;

import java.util.UUID;

import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.security.repository.OwnerMongoRepository;

public interface LandingPageRepo extends OwnerMongoRepository<LandingPage, UUID> {

}
