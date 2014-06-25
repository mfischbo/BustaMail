package de.mfischbo.bustamail.security.event;

import org.springframework.context.ApplicationEvent;

import de.mfischbo.bustamail.security.domain.OrgUnit;

/**
 * Event that is fired when a new OrgUnit has been created.
 * @author M. Fischboeck 
 *
 */
public class OrgUnitCreatedEvent extends ApplicationEvent {

	private static final long serialVersionUID = -1086231032327166993L;
	
	private OrgUnit unit;
	
	public OrgUnitCreatedEvent(Object source, OrgUnit unit) {
		super(source);
		this.unit = unit;
	}
	
	public OrgUnit getOrgUnit() {
		return this.unit;
	}
}
