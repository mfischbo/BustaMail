package de.mfischbo.bustamail.reader;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.supercsv.io.ITokenizer;
import org.supercsv.io.Tokenizer;
import org.supercsv.io.dozer.CsvDozerBeanReader;
import org.supercsv.prefs.CsvPreference;

public class CSVReader {

	public static String WIN_EOL = "\r\n";
	public static String NIX_EOL = "\n";
	
	public static CsvPreference buildPreferences(char quoteChar, char delimiter, String eolSymbols) {
		CsvPreference.Builder b = new CsvPreference.Builder(quoteChar, (int) delimiter, eolSymbols);
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
}
