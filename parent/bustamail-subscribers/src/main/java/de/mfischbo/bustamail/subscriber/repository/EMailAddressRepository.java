package de.mfischbo.bustamail.subscriber.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.mfischbo.bustamail.subscriber.domain.EMailAddress;

public interface EMailAddressRepository extends
		JpaRepository<EMailAddress, UUID>, JpaSpecificationExecutor<EMailAddress> {

}
