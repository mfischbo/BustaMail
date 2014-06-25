package de.mfischbo.bustamail.io.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;
import org.supercsv.prefs.CsvPreference;

import de.mfischbo.bustamail.reader.CSVReader;

public class CSVReaderTest {

	private String[]	fieldNames = {"firstName", "lastName", "gender", "email", "street", "zipcode", "city"};

	
	@Test
	public void canReadODTCsvUTF8SemicolonDelimitedUnquoted() throws Exception {
		InputStream in = getClass().getResourceAsStream("/odt-csv-4lines-utf8-semicolon.csv");
		CsvPreference p = CSVReader.buildPreferences('"', ';', CSVReader.NIX_EOL);
		List<ContactCompound> result = CSVReader.readFile(
				ContactCompound.class, new InputStreamReader(in), fieldNames, true, p);
		assertResults(result);
	}
	
	private void assertResults(List<ContactCompound> result) {
		assertNotNull(result);
		assertEquals(4, result.size());
		
		ContactCompound cc = result.get(0);
		assertEquals("John", 	cc.firstName);
		assertEquals("Doe",  	cc.lastName);
		assertEquals("M", 		cc.gender);
		assertEquals("john.doe@example.com", cc.email);
		assertEquals("Teststreet", cc.street);
		assertEquals("12345", 	cc.zipcode);
		assertEquals("Berlin",  cc.city);
		
		ContactCompound c2 = result.get(3);
		assertEquals("Jürgen", c2.firstName);
		assertEquals("Stürmer", c2.lastName);
		assertEquals("München", c2.city);
	}
}
