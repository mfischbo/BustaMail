package de.mfischbo.bustamail.landingpage.domain;

import de.mfischbo.bustamail.security.domain.User;
import de.mfischbo.bustamail.template.domain.Template;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.DateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AbstractHtmlPage.class)
public abstract class AbstractHtmlPage_ extends de.mfischbo.bustamail.common.domain.OwnedBaseDomain_ {

	public static volatile SingularAttribute<AbstractHtmlPage, Template> template;
	public static volatile SingularAttribute<AbstractHtmlPage, DateTime> dateCreated;
	public static volatile SingularAttribute<AbstractHtmlPage, String> name;
	public static volatile SingularAttribute<AbstractHtmlPage, String> description;
	public static volatile SingularAttribute<AbstractHtmlPage, DateTime> dateModified;
	public static volatile SingularAttribute<AbstractHtmlPage, User> userCreated;
	public static volatile SingularAttribute<AbstractHtmlPage, User> userModified;

}

