package de.mfischbo.bustamail.security.repository;

import java.util.Collection;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import de.mfischbo.bustamail.security.domain.User;

public interface UserRepository extends MongoRepository<User, ObjectId> {

	@Query("{ 'deleted' : false, '_id' : { $in : ?0 } }")
	public Page<User> findAllUsers(Collection<ObjectId> ids, Pageable page);
	
	public User findByEmail(String email);
}
