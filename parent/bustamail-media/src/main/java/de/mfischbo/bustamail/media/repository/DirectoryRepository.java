package de.mfischbo.bustamail.media.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import de.mfischbo.bustamail.media.domain.Directory;
import de.mfischbo.bustamail.security.repository.OwnerMongoRepository;

public interface DirectoryRepository extends
		OwnerMongoRepository<Directory, ObjectId> {

	@Query(" { 'owner' : { $in : ?0 }, 'parent' : null }")
	List<Directory> findByOwner(Collection<ObjectId> owners);
	
	@Query(" { 'owner' : ?1, 'parent' : ?0 } ")
	List<Directory> findByParentAndOwner(ObjectId pid, ObjectId owner);
}
