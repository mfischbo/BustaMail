package de.mfischbo.bustamail.security.repository;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OwnerJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

	public Page<T> findAllByOwner(UUID owner, Pageable page);
	
	public Page<T> findAllByOwner(UUID owner, Specifications<T> specs, Pageable page);
	
	public T findOneByOwner(UUID owner, UUID id);
}
