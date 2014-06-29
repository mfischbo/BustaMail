package de.mfischbo.bustamail.reader;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class XLSReader extends AbstractExcelReader implements ITableReader {

	public XLSReader(InputStream inStream) throws IOException {
		super(inStream);
		this.workbook = new HSSFWorkbook(this.inStream);
		this.sheet = this.workbook.getSheetAt(0);
	}
}
