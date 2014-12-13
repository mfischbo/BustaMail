package de.mfischbo.bustamail.mailing.repository;

import org.bson.types.ObjectId;

import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.security.repository.OwnerMongoRepository;

public interface MailingRepository extends OwnerMongoRepository<Mailing, ObjectId> {

}
