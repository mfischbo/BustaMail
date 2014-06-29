package de.mfischbo.bustamail.io.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import de.mfischbo.bustamail.reader.TableDataReader;

public class XLSXReaderTest {

	@Test
	public void canReadXLSXFromODT() throws Exception {
		InputStream in = getClass().getResourceAsStream("/odt-xlsx-default.xlsx");
		TableDataReader reader = new TableDataReader(in, "application/ms-office", "odt-xlsx-default.xlsx");
		List<List<String>> data = reader.getRawTableData();
		
		assertNotNull(data);
		assertEquals(5, data.size());
		
		List<String> row = data.get(4);
		assertNotNull(row);
		assertEquals(10, row.size());
		
		assertEquals("", 					row.get(0));
		assertEquals("0",					row.get(1));
		assertEquals("Karl",				row.get(2));
		assertEquals("Koch",				row.get(3));
		assertEquals("",					row.get(4));
		assertEquals("k.koch@example.com", 	row.get(5));
		assertEquals("Musterstraße 10",		row.get(6));
		assertEquals("12345",				row.get(7));
		assertEquals("Berlin",				row.get(8));
		assertEquals("DEU",					row.get(9));
	}
}
