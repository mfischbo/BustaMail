package de.mfischbo.bustamail.reader;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSXReader extends AbstractExcelReader implements ITableReader {

	public XLSXReader(InputStream inStream) throws IOException {
		super(inStream);
		this.workbook = new XSSFWorkbook(this.inStream);
		this.sheet = this.workbook.getSheetAt(0);
	}
}
