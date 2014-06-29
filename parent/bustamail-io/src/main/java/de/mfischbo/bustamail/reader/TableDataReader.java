package de.mfischbo.bustamail.reader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

public class TableDataReader {

	public enum FileType {
		CSV,
		XLS,
		XLSX,
		JSON,
		XML
	}
	
	
	private InputStream			inStream;
	private ITableReader		tableReader;
	private FileType			type;
	
	public TableDataReader(InputStream fileStream, String mimetype, String filename) throws IOException {
		this.inStream = fileStream;
		
		if (mimetype.equalsIgnoreCase("text/csv")) {
			tableReader = new CSVReader(inStream);
			type = FileType.CSV;
		}
		
		if (mimetype.equalsIgnoreCase("application/x-tika-msoffice") && filename.toLowerCase().endsWith(".xlsx")) {
			tableReader = new XLSXReader(inStream);
			type = FileType.XLSX;
		}
		
		if (tableReader == null && (filename.toLowerCase().endsWith(".csv") || filename.toLowerCase().endsWith(".txt"))) {
			tableReader = new CSVReader(inStream);
			type = FileType.CSV;
		}
		
		if (tableReader == null && filename.toLowerCase().endsWith(".xlsx")) {
			tableReader = new XLSXReader(inStream);
			type = FileType.XLSX;
		}
		
		if (tableReader == null && filename.toLowerCase().endsWith(".xls")) {
			tableReader = new XLSReader(inStream);
			type = FileType.XLS;
		}
	}
	
	public List<List<String>> getRawTableData() {
		return this.tableReader.getRawTableData();
	}
	
	public void setCsvDelimiterChar(char delimiter) {
		if (this.tableReader instanceof CSVReader)
			((CSVReader) this.tableReader).setDelimiter(delimiter);
	}
	
	public void setCsvQuoteChar(char quoteChar) {
		if (this.tableReader instanceof CSVReader)
			((CSVReader) this.tableReader).setQuoteChar(quoteChar);
	}
	
	public void setCsvEscapeChar(char escapeChar) {
		if (this.tableReader instanceof CSVReader)
			((CSVReader) this.tableReader).setEscapeChar(escapeChar);
	}
	
	public void setReaderEncoding(Charset encoding) {
		this.tableReader.setCharacterEncoding(encoding);
	}
	
	public char getEstimatedDelimiterChar() {
		if (this.tableReader instanceof CSVReader)
			return ((CSVReader) this.tableReader).getEstimatedDelimiter();
		return 0;
	}
	
	public char getEstimatedQuoteChar() {
		if (this.tableReader instanceof CSVReader)
			return ((CSVReader) this.tableReader).getEstimatedQuoteCharacter();
		return 0;
	}
	
	public FileType getType() {
		return this.type;
	}
}
