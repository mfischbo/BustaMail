package de.mfischbo.bustamail.reader;

import java.nio.charset.Charset;
import java.util.List;

public interface ITableReader {

	public List<List<String>>		getRawTableData();
	
	public void						setCharacterEncoding(Charset encoding);
}
