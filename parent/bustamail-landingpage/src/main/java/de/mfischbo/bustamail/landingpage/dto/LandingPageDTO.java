package de.mfischbo.bustamail.landingpage.dto;

import de.mfischbo.bustamail.vc.dto.VersionedContentDTO;

public class LandingPageDTO extends LandingPageIndexDTO {

	private static final long serialVersionUID = -4806411259910343701L;

	private VersionedContentDTO 		content;

	public VersionedContentDTO getContent() {
		return content;
	}

	public void setContent(VersionedContentDTO content) {
		this.content = content;
	}
}
