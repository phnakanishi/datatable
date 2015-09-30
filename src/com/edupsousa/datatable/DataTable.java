package com.edupsousa.datatable;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DataTable {

	public static final int TYPE_INT = 0;
	public static final int TYPE_STRING = 1;

	public static final int FORMAT_CSV = 0;
	public static final int FORMAT_HTML = 1;

	private Interface export;

	private LinkedHashMap<String, Integer> columnsTypes = new LinkedHashMap<String, Integer>();
	private ArrayList<DataTableRow> rows = new ArrayList<DataTableRow>();

	public int columnsCount() {
		return columnsTypes.size();
	}

	public int rowsCount() {
		return rows.size();
	}

	public void addCollumn(String name, int type) {
		columnsTypes.put(name, type);
	}

	public boolean hasCollumn(String name) {
		return columnsTypes.containsKey(name);
	}

	public DataTableRow createRow() {
		return new DataTableRow(this);
	}

	public void insertRow(DataTableRow row) {
		checkRowCompatibilityAndThrows(row);
		rows.add(row);
	}

	public DataTableRow lastRow() {
		return rows.get(rows.size() - 1);
	}

	public int getCollumnType(String collumn) {
		return columnsTypes.get(collumn);
	}

	private void checkRowCompatibilityAndThrows(DataTableRow row) {
		for (String collumnName : columnsTypes.keySet()) {
			if (row.hasValueFor(collumnName)
					&& !(isValueCompatible(columnsTypes.get(collumnName),
							row.getValue(collumnName)))) {
				throw new ClassCastException("Wrong type for collumn "
						+ collumnName + ".");
			}
		}
	}

	private boolean isValueCompatible(int type, Object value) {
		if (type == this.TYPE_INT && !(value instanceof Integer)) {
			return false;
		} else if (type == this.TYPE_STRING && !(value instanceof String)) {
			return false;
		}
		return true;
	}

	public DataTableRow getRow(int i) {
		return rows.get(i);
	}

	public String export(int format) {
		if (format == DataTable.FORMAT_CSV)
			export = new FormatoCsv();
		if (format == DataTable.FORMAT_HTML)
			export = new FormatoHtml();
		return export.export(this, columnsTypes);
	}

	public void insertRowAt(DataTableRow row, int index) {
		rows.add(index, row);
	}

	public DataTable filterEqual(String collumn, Object value) {
		DataTable dt = new DataTable();
		for (String collumnName : columnsTypes.keySet())
			dt.addCollumn(collumnName, this.getCollumnType(collumnName));
		DataTableRow row;
		for (int i = 0; i < this.rowsCount(); i++) {
			row = this.getRow(i);
			if (value == row.getValue(collumn))
				dt.insertRow(row);
		}
		return dt;
	}

	public DataTable filterNotEqual(String collumn, Object value) {
		DataTable dt = new DataTable();
		for (String collumnName : columnsTypes.keySet())
			dt.addCollumn(collumnName, this.getCollumnType(collumnName));
		DataTableRow row;
		for (int i = 0; i < this.rowsCount(); i++) {
			row = this.getRow(i);
			if (value != row.getValue(collumn))
				dt.insertRow(row);
		}
		return dt;
	}

	public DataTable sortAscending(String collumn) {
		if (columnsTypes.get(collumn) == TYPE_STRING)
			throw new ClassCastException(
					"Apenas colunas com números inteiros são ordenados.");
		DataTable saida = TabelaVaziaComMesmaColuna();
		DataTableRow[] rows = OrdenarLinha(FiltrarArray(), collumn);
		for (int i = 0; i < rows.length; i++)
			saida.insertRow(rows[i]);
		return saida;
	}

	public DataTable sortDescending(String collumn) {
		if (columnsTypes.get(collumn) == TYPE_STRING)
			throw new ClassCastException(
					"Apenas colunas com números inteiros são ordenados.");
		DataTable saida = TabelaVaziaComMesmaColuna();
		DataTableRow[] rows = OrdenarLinha2(FiltrarArray(), collumn);
		for (int i = 0; i < rows.length; i++)
			saida.insertRow(rows[i]);
		return saida;
	}

	private DataTable TabelaVaziaComMesmaColuna() {
		DataTable saida = new DataTable();
		for (String collumnName : columnsTypes.keySet()) {
			int type = columnsTypes.get(collumnName);
			saida.addCollumn(collumnName, type);
		}
		return saida;
	}

	private DataTableRow[] FiltrarArray() {
		DataTableRow[] rows = new DataTableRow[this.rowsCount()];
		for (int i = 0; i < this.rowsCount(); i++)
			rows[i] = this.getRow(i);
		return rows;
	}

	private DataTableRow[] OrdenarLinha(DataTableRow[] rows, String collumn) {
		for (int i = 0; i < rows.length - 1; i++) {
			for (int k = 0; k < rows.length - 1; k++) {
				if ((int) rows[k].getValue(collumn) > (int) rows[k + 1]
						.getValue(collumn)) {
					DataTableRow dtr = rows[k + 1];
					rows[k + 1] = rows[k];
					rows[k] = dtr;
				}
			}
		}
		return rows;
	}

	private DataTableRow[] OrdenarLinha2(DataTableRow[] rows, String collumn) {
		for (int i = 0; i < rows.length - 1; i++) {
			for (int k = 0; k < rows.length - 1; k++) {
				if ((int) rows[k].getValue(collumn) < (int) rows[k + 1]
						.getValue(collumn)) {
					DataTableRow dtr = rows[k + 1];
					rows[k + 1] = rows[k];
					rows[k] = dtr;
				}
			}
		}
		return rows;
	}
}
