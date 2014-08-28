package de.mfischbo.bustamail.landingpage.domain;

import java.util.UUID;

/**
 * Interface for html pages like landing pages and static pages
 * @author foobox
 *
 */
public interface HTMLPage {

	UUID getId();
	String getName();
	
}
