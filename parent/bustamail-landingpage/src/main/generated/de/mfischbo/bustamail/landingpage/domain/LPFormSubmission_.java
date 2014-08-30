package de.mfischbo.bustamail.landingpage.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.DateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(LPFormSubmission.class)
public abstract class LPFormSubmission_ extends de.mfischbo.bustamail.common.domain.BaseDomain_ {

	public static volatile SingularAttribute<LPFormSubmission, DateTime> dateCreated;
	public static volatile SingularAttribute<LPFormSubmission, String> sourceIP;
	public static volatile SingularAttribute<LPFormSubmission, String> data;
	public static volatile SingularAttribute<LPFormSubmission, LPForm> form;

}

