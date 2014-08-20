package de.mfischbo.bustamail.landingpage.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mfischbo.bustamail.landingpage.domain.LPForm;

public interface LPFormRepo extends JpaRepository<LPForm, UUID> {

}
