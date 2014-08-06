package de.mfischbo.bustamail.landingpage.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mfischbo.bustamail.landingpage.domain.StaticPage;

public interface StaticPageRepo extends JpaRepository<StaticPage, UUID> {

}
