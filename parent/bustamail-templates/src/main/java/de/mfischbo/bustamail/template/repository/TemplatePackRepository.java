package de.mfischbo.bustamail.template.repository;

import java.util.UUID;

import de.mfischbo.bustamail.security.repository.OwnerJpaRepository;
import de.mfischbo.bustamail.template.domain.TemplatePack;

public interface TemplatePackRepository extends
		OwnerJpaRepository<TemplatePack, UUID> {


}
