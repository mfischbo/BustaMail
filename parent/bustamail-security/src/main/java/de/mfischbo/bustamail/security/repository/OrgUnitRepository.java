package de.mfischbo.bustamail.security.repository;

import java.util.List;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import de.mfischbo.bustamail.security.domain.OrgUnit;

public interface OrgUnitRepository extends MongoRepository<OrgUnit, ObjectId> {
	
	@Query("{ 'actors.user.$id' : ?0 }")
	public List<OrgUnit> findAllWithUserAsActor(ObjectId userId);
	
	@Query(" { 'actors.user.$id' : ?0, 'actors.permissions' : { $all : ?1 } } ")
	public List<OrgUnit> findAllWithUserAsActorAndAllPermissions(ObjectId userId, List<UUID> perms);
	
	public List<OrgUnit> findByParent(OrgUnit unit);
}
