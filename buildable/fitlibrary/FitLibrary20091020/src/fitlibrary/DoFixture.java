/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary;

import java.util.List;

import fit.Parse;
import fitlibrary.suite.InFlowPageRunner;
import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.traverse.workflow.DoEvaluator;
import fitlibrary.traverse.workflow.DoTraverse;
import fitlibrary.utility.TableListener;
import fitlibrary.utility.TestResults;

/** An alternative to fit.ActionFixture
	@author rick mugridge, july 2003
  * 
  * See the specifications for examples
*/
public class DoFixture extends FitLibraryFixture implements DoEvaluator {
	private DoTraverse doTraverse = new DoTraverse(this);
	
	public DoFixture() {
    	setTraverse(doTraverse);
	}
	public DoFixture(Object sut) {
		this();
	    setSystemUnderTest(sut);
	}

	public void setTraverse(DoTraverse traverse) {
    	this.doTraverse = traverse;
    	super.setTraverse(traverse);
    }
    // Dispatched to from Fixture when a DoFixture is the first fixture in a storytest
    @Override
	final public void interpretTables(Parse tables) {
    	TestResults testResults = createTestResults();
		new InFlowPageRunner(this,testResults).run(new Tables(tables),0,new TableListener(listener,testResults),true);
    }
    // Dispatched to from Fixture when Fixture is doTabling the tables one by one (not in flow)
	@Override
	final public Object interpretAfterFirstRow(Table table, TestResults testResults) {
    	return ((DoTraverse)traverse()).interpretInFlow(table,testResults);
    }
	/**
	 * if (stopOnError) then we don't continue intepreting a table
	 * as there's been a problem
	 */
	public void setStopOnError(boolean stopOnError) {
		TestResults.setStopOnError(stopOnError);
	}
	protected void abandon() {
		TestResults.setAbandoned();
//		throw new AbandonException();
	}
	protected void showAfterTable(String s) {
		doTraverse.showAfterTable(s);
	}
	protected Object getExpectedResult() {
		return doTraverse.getExpectedResult();
	}
	protected void setExpandDefinedActions(boolean expandDefinedActions) {
		doTraverse.setExpandDefinedActions(expandDefinedActions);
	}
	public Object interpretInFlow(Table firstTable, TestResults testResults) {
		return doTraverse.interpretInFlow(firstTable,testResults);
	}
	final public Object interpretWholeTable(Table table, TableListener tableListener) {
		return doTraverse.interpretWholeTable(table,tableListener);
	}
	@Override
	public void setUp(Table firstTable, TestResults testResults) {
		doTraverse.setUp(firstTable,testResults);
	}
	@Override
	public void setUp() throws Exception {
		doTraverse.setUp();
	}
	@Override
	public void tearDown(Table firstTable, TestResults testResults) {
		doTraverse.tearDown(firstTable,testResults);
	}
	@Override
	public void tearDown() throws Exception {
		doTraverse.tearDown();
	}
	// --------- Interpretation ---------------------------------------
	protected void setGatherExpectedForGeneration(boolean gatherExpectedForGeneration) {
		doTraverse.setGatherExpectedForGeneration(gatherExpectedForGeneration);
	}
	public void setSetUpFixture(SetUpFixture setUpFixture) {
		doTraverse.setSetUpTraverse(setUpFixture.getSetUpTraverse());
	}
	void finishSettingUp() {
		doTraverse.setSettingUp(false);
	}
	public List<String> methodsThatAreVisible() {
		return doTraverse.methodsThatAreVisible();
	}
}
