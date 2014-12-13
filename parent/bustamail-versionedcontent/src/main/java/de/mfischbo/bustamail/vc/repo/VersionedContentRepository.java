package de.mfischbo.bustamail.vc.repo;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import de.mfischbo.bustamail.vc.domain.VersionedContent;
import de.mfischbo.bustamail.vc.domain.VersionedContent.ContentType;

public interface VersionedContentRepository extends MongoRepository<VersionedContent, ObjectId> {

	@Query(" { foreignId : ?0, type : { $in : ?2 } } ")
	public Page<VersionedContent> findByForeignIdAndType(ObjectId foreignId, Collection<ContentType> type, Pageable page);

	@Query(" { foreignId : ?0 } ")
	public List<VersionedContent> findByForeignId(ObjectId foreignId);
}
