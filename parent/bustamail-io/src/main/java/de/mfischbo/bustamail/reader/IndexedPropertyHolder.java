package de.mfischbo.bustamail.reader;

import java.util.ArrayList;
import java.util.List;

public class IndexedPropertyHolder {

	private List<Object> columns = new ArrayList<>();

	public List<Object> getColumns() {
		return columns;
	}

	public void setColumns(List<Object> columns) {
		this.columns = columns;
	}
}
