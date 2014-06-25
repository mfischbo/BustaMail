package de.mfischbo.bustamail.mailinglist.dto;

import java.util.UUID;

public class SubscriptionImportDTO {

	public enum FileEncoding {
		UTF8,
		UTF16,
		ISO88591,
		CP1252
	}
	
	
	/* The id of the media that has been uploaded for the import */
	private UUID 			mediaId;
	
	private FileEncoding	encoding;
	
	private boolean			headerLine;
	
	private String[]		fieldNames;
	
	public SubscriptionImportDTO() {
		
	}

	public UUID getMediaId() {
		return mediaId;
	}

	public void setMediaId(UUID mediaId) {
		this.mediaId = mediaId;
	}

	public FileEncoding getEncoding() {
		return encoding;
	}

	public void setEncoding(FileEncoding encoding) {
		this.encoding = encoding;
	}

	public boolean isHeaderLine() {
		return headerLine;
	}

	public void setHeaderLine(boolean headerLine) {
		this.headerLine = headerLine;
	}

	public String[] getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}
}
