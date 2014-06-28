package de.mfischbo.bustamail.mailinglist.dto;

import java.util.LinkedList;
import java.util.List;

import de.mfischbo.bustamail.mailinglist.validation.Validator.ResultType;

/**
 * DTO containing the data after a file got parsed for import.
 * @author M. Fischboeck
 *
 */
public class ParsingResultDTO {
	
	/* Contains the sample data of the parsing process */
	private List<List<String>>			data;

	private List<List<ResultType>>		parsingResults;

	private List<Integer>				errorLines = new LinkedList<>();
	
	public List<List<String>> getData() {
		return data;
	}

	public void setData(List<List<String>> data) {
		this.data = data;
	}

	public List<List<ResultType>> getParsingResults() {
		return parsingResults;
	}

	public void setParsingResults(List<List<ResultType>> parsingResults) {
		this.parsingResults = parsingResults;
	}

	public List<Integer> getErrorLines() {
		return errorLines;
	}

	public void setErrorLines(List<Integer> errorLines) {
		this.errorLines = errorLines;
	}
}
