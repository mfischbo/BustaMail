package de.mfischbo.bustamail.media.repository;

import org.bson.types.ObjectId;

import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.security.repository.OwnerMongoRepository;

public interface MediaRepository extends OwnerMongoRepository<Media, ObjectId> {

}
