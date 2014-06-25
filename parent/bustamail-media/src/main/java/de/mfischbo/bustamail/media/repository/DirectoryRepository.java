package de.mfischbo.bustamail.media.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.mfischbo.bustamail.media.domain.Directory;
import de.mfischbo.bustamail.security.repository.OwnerJpaRepository;

public interface DirectoryRepository extends
		OwnerJpaRepository<Directory, UUID>, JpaSpecificationExecutor<Directory> {

}
