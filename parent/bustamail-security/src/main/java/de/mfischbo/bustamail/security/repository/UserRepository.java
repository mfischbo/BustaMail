package de.mfischbo.bustamail.security.repository;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.mfischbo.bustamail.security.domain.User;

@Transactional
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

	public User findByEmail(String email);
}
