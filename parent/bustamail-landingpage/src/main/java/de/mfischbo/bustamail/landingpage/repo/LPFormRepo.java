package de.mfischbo.bustamail.landingpage.repo;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.mfischbo.bustamail.landingpage.domain.LPForm;

public interface LPFormRepo extends MongoRepository<LPForm, UUID> {

}
