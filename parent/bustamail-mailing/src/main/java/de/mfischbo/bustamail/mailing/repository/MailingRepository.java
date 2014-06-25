package de.mfischbo.bustamail.mailing.repository;

import java.util.UUID;

import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.security.repository.OwnerJpaRepository;

public interface MailingRepository extends OwnerJpaRepository<Mailing, UUID> {

}
