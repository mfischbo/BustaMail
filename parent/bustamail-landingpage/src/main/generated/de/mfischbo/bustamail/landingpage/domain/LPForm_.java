package de.mfischbo.bustamail.landingpage.domain;

import de.mfischbo.bustamail.landingpage.domain.LPForm.SubmissionAction;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(LPForm.class)
public abstract class LPForm_ extends de.mfischbo.bustamail.common.domain.BaseDomain_ {

	public static volatile SingularAttribute<LPForm, SubmissionAction> onSuccessAction;
	public static volatile SingularAttribute<LPForm, Boolean> isConversion;
	public static volatile SingularAttribute<LPForm, LandingPage> landingPage;
	public static volatile SingularAttribute<LPForm, String> name;
	public static volatile SingularAttribute<LPForm, String> redirectTarget;
	public static volatile ListAttribute<LPForm, LPFormEntry> fields;

}

