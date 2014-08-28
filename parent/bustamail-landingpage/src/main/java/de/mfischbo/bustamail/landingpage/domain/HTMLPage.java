package de.mfischbo.bustamail.landingpage.domain;

import java.util.UUID;

/**
 * Interface for html pages like landing pages and static pages.
 * @author M.Fischboeck 
 *
 */
public interface HTMLPage {

	UUID getId();
	String getName();
	
}
