package de.mfischbo.bustamail.security.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.mfischbo.bustamail.security.domain.Actor;
import de.mfischbo.bustamail.security.domain.User;

public interface ActorRepository extends JpaRepository<Actor, UUID> , JpaSpecificationExecutor<Actor> {
	public List<Actor> findByUser(User u);
}
