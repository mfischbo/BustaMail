package de.mfischbo.bustamail.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.ITokenizer;
import org.supercsv.io.Tokenizer;
import org.supercsv.io.dozer.CsvDozerBeanReader;
import org.supercsv.prefs.CsvPreference;

public class CSVReader implements ITableReader {

	public static String WIN_EOL = "\r\n";
	public static String NIX_EOL = "\n";

	private InputStream 	inStream;
	private String[]		headers;
	
	private char			delimiter = ',';
	private char			quoteChar = '"';
	
	private char			estimatedDelimiter = ';';
	private char			estimatedQuoteChar = '"';
	
	public CSVReader(InputStream inStream) throws IOException {
		this.inStream = inStream;
		initEstimation();
	}
	

	public static CsvPreference buildPreferences(char quoteChar, char delimiter, String eolSymbols) {
		CsvPreference.Builder b = new CsvPreference.Builder(quoteChar, (int) delimiter, eolSymbols);
		return b.build();
	}
	
	private CsvPreference getPreferences() {
		String eol = CsvPreference.STANDARD_PREFERENCE.getEndOfLineSymbols();
		
		CsvPreference.Builder b = new CsvPreference.Builder(quoteChar, (int) delimiter, eol);
		return b.build();
	}
	
	public static <T> List<T> readFile(Class<T> result, 
			Reader fileReader, 
			String[] fields, 
			boolean skipHeader, CsvPreference preferences) throws IOException {
		
		if (preferences == null)
			preferences = CsvPreference.STANDARD_PREFERENCE;
		
		ITokenizer tok = new Tokenizer(fileReader, preferences);
		CsvDozerBeanReader reader = new CsvDozerBeanReader(tok, preferences);
		reader.configureBeanMapping(result, fields);
	
		if (skipHeader)
			reader.getHeader(true);
		
		List<T> retval = new LinkedList<>();
		T bean = null;
		while ((bean = reader.read(result)) != null) {
			retval.add(bean);
		}
		reader.close();
		return retval;
	}
	
	
	public static List<List<String>> readFile(CsvPreference preferences, Reader fileReader) {
		
		if (preferences == null)
			preferences = CsvPreference.STANDARD_PREFERENCE;
		
		ITokenizer tok = new Tokenizer(fileReader, preferences);
		CsvListReader reader = new CsvListReader(tok, preferences);
		
		List<List<String>> retval = new LinkedList<>();
		List<String> row = null;
	
		do {
			try {
				row = reader.read();
				if (row != null)
					retval.add(row);
			} catch (Exception ex) {
				
			}
		} while (row != null);
		try {
			reader.close();
		} catch (Exception ex) {
			
		}
		return retval;
	}

	@Override
	public List<List<String>> getRawTableData() {
		InputStreamReader reader = new InputStreamReader(this.inStream);
		return CSVReader.readFile(getPreferences(), reader);
	}

	@Override
	public <T> List<T> readObjects(Class<T> classname) {
		InputStreamReader reader = new InputStreamReader(this.inStream); 
		try {
			return CSVReader.readFile(classname, reader, this.headers, true, getPreferences());
		} catch (Exception ex) {
			return null;
		}
	}

	public void setColumnHeaders(String[] headers) {
		this.headers = headers;
	}

	public void setDelimiter(char character) {
		this.delimiter = character;
	}
	
	public void setQuoteChar(char quoteChar) {
		this.quoteChar = quoteChar;
	}
	
	public void setEscapeChar(char escapeChar) {
	}
	
	private void initEstimation() throws IOException {
		int sem = 0;
		int com = 0;
		int tab = 0;

		int dq  = 0;
		int sq  = 0;
		
		try {
			byte[] buffer = new byte[2048];
			while (this.inStream.read(buffer) > -1) {
				String s = new String(buffer);
				sem += (s.split(";").length -1);
				com += (s.split(",").length -1);
				tab += (s.split("\t").length -1);
				
				dq += (s.split("\"").length -1);
				sq += (s.split("'").length  -1);
				
				buffer = new byte[2048];
			}
		} catch (Exception ex) {
		}
		
		if (sem >= com && sem >= tab)
			this.estimatedDelimiter = ';';
		if (com >= tab && com >= sem)
			this.estimatedDelimiter = ',';
		if (tab >= sem && tab >= com)
			this.estimatedDelimiter = 9;
		
		if (sq > dq) this.estimatedQuoteChar = '\'';
		
		this.inStream.reset();
	}

	public char getEstimatedDelimiter() {
		return this.estimatedDelimiter;
	}
	
	public char getEstimatedQuoteCharacter() {
		return this.estimatedQuoteChar;
	}
	
	@Override
	public void setCharacterEncoding(Charset encoding) {
	}
}
