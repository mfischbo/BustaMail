package de.mfischbo.bustamail.landingpage.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "LandingPages_StaticPage")
public class StaticPage extends AbstractHtmlPage implements HTMLPage {

	private static final long serialVersionUID = 8737051906342406125L;


	@ManyToOne
	@JoinColumn(name = "LandingPage_id", referencedColumnName = "id", nullable = false)
	private LandingPage		parent;
	
	public LandingPage getParent() {
		return parent;
	}

	public void setParent(LandingPage parent) {
		this.parent = parent;
	}
}
