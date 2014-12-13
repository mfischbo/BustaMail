package de.mfischbo.bustamail.landingpage.domain;

import org.bson.types.ObjectId;

/**
 * Interface for html pages like landing pages and static pages.
 * @author M.Fischboeck 
 *
 */
public interface HTMLPage {

	ObjectId getId();
	String getName();
	
}
