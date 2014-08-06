package de.mfischbo.bustamail.landingpage.dto;

import de.mfischbo.bustamail.vc.dto.VersionedContentDTO;

public class StaticPageDTO extends StaticPageIndexDTO {

	private static final long serialVersionUID = 3598978167133094098L;

	private VersionedContentDTO		content;

	public VersionedContentDTO getContent() {
		return content;
	}

	public void setContent(VersionedContentDTO content) {
		this.content = content;
	}
}
