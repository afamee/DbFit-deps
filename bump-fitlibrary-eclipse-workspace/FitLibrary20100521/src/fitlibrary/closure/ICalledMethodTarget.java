/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.closure;

import fitlibrary.parser.Parser;
import fitlibrary.runResults.TestResults;
import fitlibrary.table.Cell;
import fitlibrary.table.Row;
import fitlibrary.traverse.workflow.DoTraverse.Comparison;

public interface ICalledMethodTarget extends MethodTarget {
	Object invoke(Object[] arguments) throws Exception;
	Class<?> getReturnType();
	void invokeAndCheckForSpecial(Row rowFrom, Cell expectedCell,
			TestResults testResults, Row row, Cell cell);
	Object getResult(Cell expectedCell, TestResults testResults);
	public Object invokeForSpecial(Row row, TestResults testResults, 
			boolean catchParseError, Cell operatorCell) throws Exception;
	void notResult(Cell expectedCell, Object result, TestResults testResults);
	Object invoke(Row row, TestResults testResults, boolean catchParseError) throws Exception;
	public void compare(Cell expectedCell, Comparable actual, TestResults testResults, Comparison compare);
	Parser getResultParser();
	String getResultString(Object result) throws Exception;
}
