package de.mfischbo.bustamail.landingpage.repo;

import java.util.UUID;

import de.mfischbo.bustamail.landingpage.domain.LandingPage;
import de.mfischbo.bustamail.security.repository.OwnerJpaRepository;

public interface LandingPageRepo extends OwnerJpaRepository<LandingPage, UUID> {

}
