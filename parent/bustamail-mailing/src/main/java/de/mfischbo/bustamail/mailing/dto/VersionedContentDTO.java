package de.mfischbo.bustamail.mailing.dto;

public class VersionedContentDTO extends VersionedContentIndexDTO {

	private static final long serialVersionUID = -5475100755406102245L;

	private String				content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
