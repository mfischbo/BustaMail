package de.mfischbo.bustamail.reader;

import java.nio.charset.Charset;
import java.util.List;

public interface ITableReader {

	public List<List<String>>		getRawTableData();
	
	public <T> List<T>				readObjects(Class<T> classname);
	
	public void 					setColumnHeaders(String[] headers);
	
	public void						setCharacterEncoding(Charset encoding);
}
