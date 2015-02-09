package de.mfischbo.bustamail.optin.repository;

import org.bson.types.ObjectId;

import de.mfischbo.bustamail.optin.domain.OptinMail;
import de.mfischbo.bustamail.security.repository.OwnerMongoRepository;

public interface OptinMailRepo extends OwnerMongoRepository<OptinMail, ObjectId> {

}
