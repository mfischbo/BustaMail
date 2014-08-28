package de.mfischbo.bustamail.landingpage.dto;

import de.mfischbo.bustamail.vc.dto.VersionedContentDTO;

public class StaticPageDTO extends StaticPageIndexDTO {

	private static final long serialVersionUID = 3598978167133094098L;

	private VersionedContentDTO		htmlContent;

	public VersionedContentDTO getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(VersionedContentDTO content) {
		this.htmlContent = content;
	}
}
