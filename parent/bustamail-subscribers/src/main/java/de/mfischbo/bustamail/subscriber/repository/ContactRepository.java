package de.mfischbo.bustamail.subscriber.repository;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mfischbo.bustamail.subscriber.domain.Contact;

@Transactional
public interface ContactRepository extends JpaRepository<Contact, UUID> {

}
