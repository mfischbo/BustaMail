package de.mfischbo.bustamail.mailinglist.dto;

import java.util.Arrays;

import org.bson.types.ObjectId;

import de.mfischbo.bustamail.reader.TableDataReader.FileType;

public class SubscriptionImportDTO {

	
	/* The id of the media that has been uploaded for the import */
	private ObjectId		mediaId;
	
	private String			encoding;
	
	private boolean			containsHeader;
	
	private String[]		fieldNames;
	
	private FileType		type;
	
	private char 			csvDelimiter;
	private char			csvQuoteChar;
	
	/* states if subscribers having the same email should be overriden */
	private boolean			override;

	/* states if issues in the data should be ignored */
	private boolean			ignoreIssues;
	
	public SubscriptionImportDTO() {
		
	}

	public ObjectId getMediaId() {
		return mediaId;
	}

	public void setMediaId(ObjectId mediaId) {
		this.mediaId = mediaId;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isContainsHeader() {
		return containsHeader;
	}

	public void setContainsHeader(boolean headerLine) {
		this.containsHeader = headerLine;
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
	
	public boolean isIgnoreIssues() {
		return ignoreIssues;
	}

	public void setIgnoreIssues(boolean ignoreIssues) {
		this.ignoreIssues = ignoreIssues;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (containsHeader ? 1231 : 1237);
		result = prime * result + csvDelimiter;
		result = prime * result + csvQuoteChar;
		result = prime * result
				+ ((encoding == null) ? 0 : encoding.hashCode());
		result = prime * result + Arrays.hashCode(fieldNames);
		result = prime * result + ((mediaId == null) ? 0 : mediaId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubscriptionImportDTO other = (SubscriptionImportDTO) obj;
		if (containsHeader != other.containsHeader)
			return false;
		if (csvDelimiter != other.csvDelimiter)
			return false;
		if (csvQuoteChar != other.csvQuoteChar)
			return false;
		if (encoding == null) {
			if (other.encoding != null)
				return false;
		} else if (!encoding.equals(other.encoding))
			return false;
		return true;
	}
}
