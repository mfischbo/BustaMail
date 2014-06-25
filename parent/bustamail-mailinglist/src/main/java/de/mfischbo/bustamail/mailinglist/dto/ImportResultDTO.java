package de.mfischbo.bustamail.mailinglist.dto;

public class ImportResultDTO {

	private int		linesRecognized;
	private int		errorLines;
	private int		successLines;
	

	public ImportResultDTO() {
		
	}


	public int getLinesRecognized() {
		return linesRecognized;
	}


	public void setLinesRecognized(int linesRecognized) {
		this.linesRecognized = linesRecognized;
	}


	public int getErrorLines() {
		return errorLines;
	}


	public void setErrorLines(int errorLines) {
		this.errorLines = errorLines;
	}


	public int getSuccessLines() {
		return successLines;
	}


	public void setSuccessLines(int successLines) {
		this.successLines = successLines;
	}
}
