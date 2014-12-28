package de.mfischbo.bustamail.template.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import de.mfischbo.bustamail.security.repository.OwnerMongoRepository;
import de.mfischbo.bustamail.template.domain.TemplatePack;

public interface TemplatePackRepository extends
		OwnerMongoRepository<TemplatePack, ObjectId> {
	
	@Query(" { 'templates._id' : ?0 } ")
	TemplatePack findByTemplateWithId(ObjectId templateId);
	
	@Query(" { 'templates.widgets._id' : ?0 } ")
	TemplatePack findByWidgetWithId(ObjectId widgetId);
}
