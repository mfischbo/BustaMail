package de.mfischbo.bustamail.landingpage.dto;

import java.util.List;

import de.mfischbo.bustamail.landingpage.domain.LPForm;
import de.mfischbo.bustamail.media.domain.Media;
import de.mfischbo.bustamail.vc.dto.VersionedContentDTO;

public class LandingPageDTO extends LandingPageIndexDTO {

	private static final long serialVersionUID = -4806411259910343701L;

	private String 						htmlHeader;
	private List<StaticPageDTO>			staticPages;
	private List<LPForm>				forms;
	private List<Media>					resources;
	private VersionedContentDTO 		htmlContent;


	public String getHtmlHeader() {
		return htmlHeader;
	}

	public void setHtmlHeader(String htmlHeader) {
		this.htmlHeader = htmlHeader;
	}

	public List<StaticPageDTO> getStaticPages() {
		return staticPages;
	}

	public void setStaticPages(List<StaticPageDTO> staticPages) {
		this.staticPages = staticPages;
	}

	public VersionedContentDTO getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(VersionedContentDTO content) {
		this.htmlContent = content;
	}

	public List<LPForm> getForms() {
		return forms;
	}

	public void setForms(List<LPForm> forms) {
		this.forms = forms;
	}

	public List<Media> getResources() {
		return resources;
	}

	public void setResources(List<Media> resources) {
		this.resources = resources;
	}
}
