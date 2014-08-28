package de.mfischbo.bustamail.landingpage.domain;

import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.template.domain.Template;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.DateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(StaticPage.class)
public abstract class StaticPage_ extends de.mfischbo.bustamail.common.domain.BaseDomain_ {

	public static volatile SingularAttribute<StaticPage, Template> template;
	public static volatile SingularAttribute<StaticPage, LandingPage> parent;
	public static volatile SingularAttribute<StaticPage, DateTime> dateCreated;
	public static volatile SingularAttribute<StaticPage, String> name;
	public static volatile SingularAttribute<StaticPage, String> description;
	public static volatile SingularAttribute<StaticPage, DateTime> dateModified;
	public static volatile SingularAttribute<StaticPage, User> userCreated;
	public static volatile SingularAttribute<StaticPage, User> userModified;

}

