package de.mfischbo.bustamail.landingpage.domain;

import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.template.domain.Template;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.DateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(LandingPage.class)
public abstract class LandingPage_ extends de.mfischbo.bustamail.common.domain.OwnedBaseDomain_ {

	public static volatile SingularAttribute<LandingPage, DateTime> datePublished;
	public static volatile SingularAttribute<LandingPage, Template> template;
	public static volatile SingularAttribute<LandingPage, DateTime> dateCreated;
	public static volatile SingularAttribute<LandingPage, User> userPublished;
	public static volatile SingularAttribute<LandingPage, String> name;
	public static volatile SingularAttribute<LandingPage, String> description;
	public static volatile SingularAttribute<LandingPage, DateTime> dateModified;
	public static volatile ListAttribute<LandingPage, StaticPage> staticPages;
	public static volatile SingularAttribute<LandingPage, User> userCreated;
	public static volatile ListAttribute<LandingPage, LPForm> forms;
	public static volatile SingularAttribute<LandingPage, User> userModified;

}

