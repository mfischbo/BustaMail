package de.mfischbo.bustamail.vc.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.mfischbo.bustamail.vc.domain.VersionedContent;

public interface VersionedContentRepository extends
		JpaRepository<VersionedContent, UUID>,
		JpaSpecificationExecutor<VersionedContent> {

}
