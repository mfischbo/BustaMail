package de.mfischbo.bustamail.media.repository;

import java.util.UUID;

import de.mfischbo.bustamail.media.domain.MediaImage;
import de.mfischbo.bustamail.security.repository.OwnerJpaRepository;

public interface MediaImageRepository extends
		OwnerJpaRepository<MediaImage, UUID> {

}
