package de.mfischbo.bustamail.landingpage.repo;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.mfischbo.bustamail.landingpage.domain.StaticPage;

public interface StaticPageRepo extends MongoRepository<StaticPage, UUID> {

}
