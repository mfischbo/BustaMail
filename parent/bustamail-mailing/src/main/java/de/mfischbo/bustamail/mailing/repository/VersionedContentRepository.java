package de.mfischbo.bustamail.mailing.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.mfischbo.bustamail.mailing.domain.VersionedContent;

public interface VersionedContentRepository extends
		JpaRepository<VersionedContent, UUID>,
		JpaSpecificationExecutor<VersionedContent> {

}
