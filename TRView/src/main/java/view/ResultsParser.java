package main.java.view;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;
import org.jboss.reddeer.common.logging.Logger;

import com.mcoufal.inrunjunit.server.ResultsData;
import com.mcoufal.inrunjunit.server.StringTestCaseElement;
import com.mcoufal.inrunjunit.server.StringTestRunSession;

/**
 * This class provides methods useful for parsing and displaying progress of a
 * test run.
 *
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 */
public class ResultsParser {
	// set up logger
	private final static Logger log = Logger.getLogger(ResultsParser.class);

	/**
	 * This method processes test results in initial data set from ResultsServer
	 * and displays them in TRView.
	 * 
	 * @param initialDataSet
	 */
	public static void parseAndDisplay(List<ResultsData> initialDataSet) {
		log.info("parsing initial data set: " + initialDataSet.toString());
		// for every result in the list : parse and display results
		for (ResultsData resultsData : initialDataSet) {
			parseAndDisplay(resultsData);
		}
	}

	/**
	 * This method processes test results in ResultsData based on Phase of
	 * current ResultsData and displays them in TRView.
	 * 
	 * @param newData
	 */
	public static void parseAndDisplay(ResultsData newData) {
		log.info("parsing data from phase: " + newData.getPhase());
		// parse and display based on current phase
		switch (newData.getPhase()) {
		case SESSION_LAUNCHED:
			parseAndDisplaySessionLaunched(newData.getTestRunSession());
			break;
		case SESSION_STARTED:
			parseAndDisplaySessionStarted(newData.getTestRunSession());
			break;
		case SESSION_FINISHED:
			parseAndDisplaySessionFinished(newData.getTestRunSession());
			break;
		case CASE_STARTED:
			parseAndDisplayCaseStarted(newData.getTestCaseElement());
			break;
		case CASE_FINISHED:
			parseAndDisplayCaseFinished(newData.getTestCaseElement());
			break;
		}
		// call redraw method on all needful components
		TRView.redrawAllComponents();
	}

	/**
	 * This method parses StringTestCaseElement received in CASE_FINISHED phase
	 * and displays results in TRView according to the phase.
	 *
	 * @param testCaseElement
	 */
	private static void parseAndDisplayCaseFinished(StringTestCaseElement testCaseElement) {
		log.debug("parsing CASE FINISHED");
		int errNum = Integer.parseInt(TRView.getTxtErrors().getText());
		int failNum = Integer.parseInt(TRView.getTxtFailures().getText());
		int ignoredNum = Integer.parseInt(TRView.getTxtFailures().getText());
		String itemText = null;

		// parsing

		// highlighting and changing icons
		TreeItem treeItems[] = TRView.getTree().getItems();
		for (TreeItem treeItem : treeItems) {
			if (treeItem.getText().equals(testCaseElement.getTestMethodName())) {
				// clear background
				treeItem.setBackground(null);
				itemText = String.format("%s (%.3fs)", testCaseElement.getTestMethodName(), testCaseElement.getElapsedTime());
				treeItem.setText(itemText);
				if (testCaseElement.getTestResultNoChildren().getResult().equals("Error")){
					treeItem.setForeground(TRView.getTree().getDisplay().getSystemColor(SWT.COLOR_RED));
					treeItem.setImage(new Image(TRView.getDisplay(), "icons/testerr.png"));
					errNum++;
					TRView.getTxtErrors().setText(Integer.toString(errNum));
				} else if (testCaseElement.getTestResultNoChildren().getResult().equals("Failure")){
					treeItem.setForeground(TRView.getTree().getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
					treeItem.setImage(new Image(TRView.getDisplay(), "icons/testfail.png"));
					failNum++;
					TRView.getTxtFailures().setText(Integer.toString(failNum));
				} else if (testCaseElement.getTestResultNoChildren().getResult().equals("Ignored")){
					treeItem.setForeground(TRView.getTree().getDisplay().getSystemColor(SWT.COLOR_GRAY));
					treeItem.setImage(new Image(TRView.getDisplay(), "icons/testignored.png"));
					ignoredNum++;
					TRView.getTxtIgnored().setText(Integer.toString(ignoredNum));
				} else if (testCaseElement.getTestResultNoChildren().getResult().equals("OK")){
					treeItem.setForeground(TRView.getTree().getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
					treeItem.setImage(new Image(TRView.getDisplay(), "icons/testok.png"));
				} else { // "Undefined"
					log.debug("inside Undefined");
					treeItem.setForeground(TRView.getTree().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
					treeItem.setImage(new Image(TRView.getDisplay(), "icons/testassumptionfailed"));
				}
			}
		}
	}

	/**
	 * This method parses StringTestCaseElement received in CASE_STARTED phase
	 * and displays results in TRView according to the phase.
	 *
	 * @param testCaseElement
	 */
	private static void parseAndDisplayCaseStarted(StringTestCaseElement testCaseElement) {
		// TODO: If Tree display on start fails, add at least parent container and class name
		log.info("parsing CASE STARTED");

		// parsing
		String lblRuns = null;
		String lblRunsArray[] = TRView.getTxtRuns().getText().split("/");
		int lblRunsRunned = Integer.parseInt(lblRunsArray[0]);

		// increase lblRuns
		lblRunsRunned++;
		lblRuns = lblRunsRunned + "/" + lblRunsArray[1];
		TRView.getTxtRuns().setText(lblRuns);

		// highlight current item
		TreeItem t1 = new TreeItem(TRView.getTree(), 0);
		t1.setText(testCaseElement.getTestMethodName());
		t1.setImage(new Image(TRView.getDisplay(), "icons/testrun.png"));

		// scroll to currently running test case (so it is visible)
		TreeItem[] currentlySelected = TRView.getTree().getSelection();
		TRView.getTree().select(t1);
		TRView.getTree().showSelection();
		TRView.getTree().setSelection(currentlySelected);
	}

	/**
	 * This method parses StringTestRunSession received in SESSION_FINISHED
	 * phase and displays results in TRView according to the phase.
	 *
	 * @param testRunSession
	 */
	private static void parseAndDisplaySessionFinished(StringTestRunSession testRunSession) {
		log.info("parsing SESSION FINISHED");
	}

	/**
	 * This method parses StringTestRunSession received in SESSION_STARTED phase
	 * and displays results in TRView according to the phase.
	 *
	 * @param testRunSession
	 */
	private static void parseAndDisplaySessionStarted(StringTestRunSession testRunSession) {
		log.info("parsing SESSION STARTED");
	}

	/**
	 * This method parses StringTestRunSession received in SESSION_LAUNCHED
	 * phase and displays results in TRView according to the phase.
	 *
	 * @param testRunSession
	 */
	private static void parseAndDisplaySessionLaunched(StringTestRunSession testRunSession) {
		log.debug("parsing SESSION LAUNCHED");

		// local variables
		int numberOfRuns = testRunSession.getChildrenElements().size();
		log.debug("numberOfChildren: " + numberOfRuns);
		TRView.getTxtRuns().setText("0/?");
		// parsing
		/*
		 * TRView.getLblRuns().setText("0/" + numberOfRuns); TreeItem t1 = new
		 * TreeItem(TRView.getTree(), 0);
		 * t1.setText(testRunSession.getTestRunName()); for (StringTestElement
		 * element : testRunSession.getChildrenElements()) { TreeItem t2 = new
		 * TreeItem(t1, 0); t2.setText(element.getTestElement()); }
		 */
	}
}
