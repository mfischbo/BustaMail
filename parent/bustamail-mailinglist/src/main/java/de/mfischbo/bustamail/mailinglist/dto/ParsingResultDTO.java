package de.mfischbo.bustamail.mailinglist.dto;

import java.util.List;

/**
 * DTO containing the data after a file got parsed for import.
 * @author M. Fischboeck
 *
 */
public class ParsingResultDTO {
	
	/* Contains the sample data of the parsing process */
	private List<List<String>>		data;

	private int[]					errorLineNumbers;
	
	private String[]				errorLines;

	public List<List<String>> getData() {
		return data;
	}

	public void setData(List<List<String>> data) {
		this.data = data;
	}

	public int[] getErrorLineNumbers() {
		return errorLineNumbers;
	}

	public void setErrorLineNumbers(int[] errorLineNumbers) {
		this.errorLineNumbers = errorLineNumbers;
	}

	public String[] getErrorLines() {
		return errorLines;
	}

	public void setErrorLines(String[] errorLines) {
		this.errorLines = errorLines;
	}
}
