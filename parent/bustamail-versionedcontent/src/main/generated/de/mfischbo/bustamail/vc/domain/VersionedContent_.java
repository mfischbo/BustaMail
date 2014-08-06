package de.mfischbo.bustamail.vc.domain;

import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.vc.domain.VersionedContent.ContentType;
import java.util.UUID;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.DateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(VersionedContent.class)
public abstract class VersionedContent_ extends de.mfischbo.bustamail.common.domain.BaseDomain_ {

	public static volatile SingularAttribute<VersionedContent, DateTime> dateCreated;
	public static volatile SingularAttribute<VersionedContent, UUID> mailingId;
	public static volatile SingularAttribute<VersionedContent, ContentType> type;
	public static volatile SingularAttribute<VersionedContent, User> userCreated;
	public static volatile SingularAttribute<VersionedContent, String> content;

}

