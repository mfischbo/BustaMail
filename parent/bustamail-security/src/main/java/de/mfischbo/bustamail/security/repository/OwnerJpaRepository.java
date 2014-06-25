package de.mfischbo.bustamail.security.repository;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OwnerJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

	public Page<T> findAllByOwner(UUID owner, Pageable page);
	
	public T findOneByOwner(UUID owner, UUID id);
}
