package de.mfischbo.bustamail.reader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public abstract class AbstractExcelReader implements ITableReader {

	protected	InputStream		inStream;
	protected	Charset			encoding;
	protected	Workbook		workbook;
	protected	Sheet			sheet;

	private static final int		MIN_COLS_TO_READ = 10;
	
	public AbstractExcelReader(InputStream inStream) throws IOException {
		this.inStream = inStream;
	}
	
	@Override
	public List<List<String>> getRawTableData() {
		
		List<List<String>> data = new ArrayList<>(this.sheet.getLastRowNum()+1);
		Iterator<Row> rit = this.sheet.iterator();
		while (rit.hasNext()) {
			Row row = rit.next();
		
			
			int maxColNum = Math.max(row.getLastCellNum(), MIN_COLS_TO_READ);
			List<String> rowData = new ArrayList<>(maxColNum);
			
			for (int cmin = 0; cmin < maxColNum; cmin++) {	
				Cell c = row.getCell(cmin, Row.CREATE_NULL_AS_BLANK);
				c.setCellType(Cell.CELL_TYPE_STRING);
				rowData.add(c.getStringCellValue());
			}
			data.add(rowData);
		}
		return data;
	}
	
	@Override
	public void setCharacterEncoding(Charset encoding) {
		this.encoding = encoding;
	}

}
