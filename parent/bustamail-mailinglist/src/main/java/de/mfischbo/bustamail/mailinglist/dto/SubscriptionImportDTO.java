package de.mfischbo.bustamail.mailinglist.dto;

import java.util.UUID;

import de.mfischbo.bustamail.reader.TableDataReader.FileType;

public class SubscriptionImportDTO {

	
	/* The id of the media that has been uploaded for the import */
	private UUID 			mediaId;
	
	private String			encoding;
	
	private boolean			headerLine;
	
	private String[]		fieldNames;
	
	private FileType		type;
	
	private char 			csvDelimiter;
	private char			csvQuoteChar;
	
	/* states if subscribers having the same email should be overriden */
	private boolean			override;
	
	public SubscriptionImportDTO() {
		
	}

	public UUID getMediaId() {
		return mediaId;
	}

	public void setMediaId(UUID mediaId) {
		this.mediaId = mediaId;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
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

	public char getCsvDelimiter() {
		return csvDelimiter;
	}

	public void setCsvDelimiter(char csvDelimiter) {
		this.csvDelimiter = csvDelimiter;
	}

	public char getCsvQuoteChar() {
		return csvQuoteChar;
	}

	public void setCsvQuoteChar(char csvQuoteChar) {
		this.csvQuoteChar = csvQuoteChar;
	}

	public FileType getType() {
		return type;
	}

	public void setType(FileType type) {
		this.type = type;
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}
}
