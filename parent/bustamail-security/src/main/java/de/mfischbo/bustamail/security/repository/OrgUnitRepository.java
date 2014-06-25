package de.mfischbo.bustamail.security.repository;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.mfischbo.bustamail.security.domain.OrgUnit;

@Transactional
public interface OrgUnitRepository extends JpaRepository<OrgUnit, UUID>, JpaSpecificationExecutor<OrgUnit> {

}
