package fitlibrary.traverse.workflow;

import fitlibrary.exception.FitLibraryException;
import fitlibrary.table.Row;
import fitlibrary.table.Table;
import fitlibrary.traverse.Traverse;
import fitlibrary.utility.TestResults;

public class SetVariableFixture extends Traverse {
	public Object interpretAfterFirstRow(Table table, TestResults testResults) {
		try {
			for (int rowNo = 1; rowNo < table.size(); rowNo++)
				processRow(table.row(rowNo), testResults);
		} catch (Exception e) {
			table.error(testResults, e);
		}
		return null;
	}
	private void processRow(Row row, TestResults testResults) {
		if (row.size() == 2)
			getDynamicVariables().put(row.text(0,this), row.text(1,this));
		else
			row.cell(0).error(testResults, new FitLibraryException("Must be two cells"));
	}
}