package de.mfischbo.bustamail.landingpage.domain;

import de.mfischbo.bustamail.landingpage.domain.LPFormEntry.ValidationType;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(LPFormEntry.class)
public abstract class LPFormEntry_ {

	public static volatile SingularAttribute<LPFormEntry, String> regexp;
	public static volatile SingularAttribute<LPFormEntry, ValidationType> validationType;
	public static volatile SingularAttribute<LPFormEntry, String> name;
	public static volatile SingularAttribute<LPFormEntry, Boolean> required;

}

