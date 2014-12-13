package de.mfischbo.bustamail.media.repository;

import org.bson.types.ObjectId;

import de.mfischbo.bustamail.media.domain.MediaImage;
import de.mfischbo.bustamail.security.repository.OwnerMongoRepository;

public interface MediaImageRepository extends
		OwnerMongoRepository<MediaImage, ObjectId> {

}
