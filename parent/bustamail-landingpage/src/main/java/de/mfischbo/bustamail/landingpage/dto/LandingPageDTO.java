package de.mfischbo.bustamail.landingpage.dto;

import java.util.List;

import de.mfischbo.bustamail.vc.dto.VersionedContentDTO;

public class LandingPageDTO extends LandingPageIndexDTO {

	private static final long serialVersionUID = -4806411259910343701L;

	private List<StaticPageDTO>			staticPages;
	private List<LPFormDTO>				forms;
	private VersionedContentDTO 		htmlContent;
	
	public List<StaticPageDTO> getStaticPages() {
		return staticPages;
	}

	public void setStaticPages(List<StaticPageDTO> staticPages) {
		this.staticPages = staticPages;
	}

	public List<LPFormDTO> getForms() {
		return forms;
	}

	public void setForms(List<LPFormDTO> forms) {
		this.forms = forms;
	}

	public VersionedContentDTO getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(VersionedContentDTO content) {
		this.htmlContent = content;
	}
}
