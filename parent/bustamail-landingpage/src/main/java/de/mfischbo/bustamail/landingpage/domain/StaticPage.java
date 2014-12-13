package de.mfischbo.bustamail.landingpage.domain;


public class StaticPage extends AbstractHtmlPage implements HTMLPage {

	private static final long serialVersionUID = 8737051906342406125L;

	private LandingPage		parent;
	
	public LandingPage getParent() {
		return parent;
	}

	public void setParent(LandingPage parent) {
		this.parent = parent;
	}
}
