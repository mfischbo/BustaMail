package de.mfischbo.bustamail.landingpage.domain;

import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.security.domain.User;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.DateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(LandingPage.class)
public abstract class LandingPage_ extends de.mfischbo.bustamail.landingpage.domain.AbstractHtmlPage_ {

	public static volatile SingularAttribute<LandingPage, DateTime> datePublished;
	public static volatile SingularAttribute<LandingPage, User> userPublished;
	public static volatile ListAttribute<LandingPage, Media> resources;
	public static volatile ListAttribute<LandingPage, StaticPage> staticPages;
	public static volatile ListAttribute<LandingPage, LPForm> forms;

}

