package de.mfischbo.bustamail.media.repository;

import java.util.UUID;

import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.security.repository.OwnerJpaRepository;

public interface MediaRepository extends OwnerJpaRepository<Media, UUID> {

}
