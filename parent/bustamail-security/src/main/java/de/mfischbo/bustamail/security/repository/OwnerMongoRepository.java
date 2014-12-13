package de.mfischbo.bustamail.security.repository;

import java.io.Serializable;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OwnerMongoRepository<T, ID extends Serializable> extends MongoRepository<T, ID> {

	public Page<T> findAllByOwner(ObjectId owner, Pageable page);
	
	public T findOneByOwner(UUID owner, UUID id);
}
