package main.java.view;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;
import org.jboss.reddeer.common.logging.Logger;

import mcoufal.inrunjunit.server.ResultsData;
import mcoufal.inrunjunit.server.StringTestCase;
import mcoufal.inrunjunit.server.StringTestCaseElement;
import mcoufal.inrunjunit.server.StringTestRunSession;

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
		StringTestCase tc = testCaseElement.getTestCase();
		int errNum = Integer.parseInt(TRView.getTxtErrors().getText());
		int failNum = Integer.parseInt(TRView.getTxtFailures().getText());
		int ignoredNum = Integer.parseInt(TRView.getTxtFailures().getText());
		String itemText = null;

		// parsing

		// highlighting and changing icons
		TreeItem treeItem = findTreeItemCaseName(tc, true);
		if (treeItem != null) {
			itemText = String.format("%s (%.3fs)", tc.getName(), testCaseElement.getElapsedTime());
			treeItem.setText(itemText);
			if (testCaseElement.getTestResultNoChildren().getResult().equals("Error")) {
				treeItem.setForeground(TRView.getTree().getDisplay().getSystemColor(SWT.COLOR_RED));
				treeItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/testerr.png")));
				errNum++;
				TRView.getTxtErrors().setText(Integer.toString(errNum));
			} else if (testCaseElement.getTestResultNoChildren().getResult().equals("Failure")) {
				treeItem.setForeground(TRView.getTree().getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
				treeItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/testfail.png")));
				failNum++;
				TRView.getTxtFailures().setText(Integer.toString(failNum));
			} else if (testCaseElement.getTestResultNoChildren().getResult().equals("Ignored")) {
				treeItem.setForeground(TRView.getTree().getDisplay().getSystemColor(SWT.COLOR_GRAY));
				treeItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/testignored.gif")));
				ignoredNum++;
				TRView.getTxtIgnored().setText(Integer.toString(ignoredNum));
			} else if (testCaseElement.getTestResultNoChildren().getResult().equals("OK")) {
				treeItem.setForeground(TRView.getTree().getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
				treeItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/testok.png")));
			} else { // "Undefined"
				treeItem.setForeground(TRView.getTree().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
				treeItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/testassumptionfailed.gif")));
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
		log.info("parsing CASE STARTED");
		StringTestCase tc = testCaseElement.getTestCase();
		testCaseElement.print();
		// parsing
		String lblRuns = null;
		String lblRunsArray[] = TRView.getTxtRuns().getText().split("/");
		int lblRunsRunned = Integer.parseInt(lblRunsArray[0]);

		// increase lblRuns
		lblRunsRunned++;
		lblRuns = lblRunsRunned + "/" + lblRunsArray[1];
		TRView.getTxtRuns().setText(lblRuns);

		// highlight current item
		TreeItem t1 = findTreeItemCaseName(tc, true);
		if (t1 != null)
			t1.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/testrun.png")));

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
		String testRunName = testRunSession.getTestRunName();
		TreeItem testSuiteItem = findTreeItemSuite(testRunName);
		if (testSuiteItem != null)
			if (testRunSession.getTestResultWithChildren().getResult().equals("OK")){
				testSuiteItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/tsuiteok.png")));
			}
			else if (testRunSession.getTestResultWithChildren().getResult().equals("Error")) {
				testSuiteItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/tsuiteerror.png")));
			}
			else if (testRunSession.getTestResultWithChildren().getResult().equals("Failure")) {
				testSuiteItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/tsuitefail.png")));
			}
	}

	/**
	 * This method parses StringTestRunSession received in SESSION_STARTED phase
	 * and displays results in TRView according to the phase.
	 *
	 * @param testRunSession
	 */
	private static void parseAndDisplaySessionStarted(StringTestRunSession testRunSession) {
		log.info("parsing SESSION STARTED");
		for (TreeItem suiteNode : TRView.getTree().getItems()) {
			if (suiteNode.getText().equals(testRunSession.getTestRunName())) {
				suiteNode.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/tsuiterun.png")));
				suiteNode.setExpanded(true);
			}
		}
	}

	/**
	 * This method parses StringTestRunSession received in SESSION_LAUNCHED
	 * phase and displays results in TRView according to the phase.
	 *
	 * @param testRunSession
	 */
	private static void parseAndDisplaySessionLaunched(StringTestRunSession testRunSession) {
		log.debug("parsing SESSION LAUNCHED");
		List<StringTestCase> testCases = testRunSession.getTestCases();
		TreeItem parentNodeItem = null;
		// parsing
		// set number of runs, errors, failures and ignored
		int numberOfRuns = testCases.size();
		log.debug("NUMBER OF RUNS: " + numberOfRuns);
		TRView.getTxtRuns().setText("0/" + numberOfRuns);
		TRView.getTxtErrors().setText("0");
		TRView.getTxtFailures().setText("0");
		TRView.getTxtIgnored().setText("0");
		// create tree of a test run
		// for every test case
		for (StringTestCase tc : testCases) {
			log.debug(String.format("parsing TC - [%s][%s][%s][%s]", tc.getName(), tc.getJavaFile(),
					tc.getPackageName(), tc.getTestSuite()));
			// test suite already exists
			if (findTreeItemSuite(tc.getTestSuite()) != null) {
				// package already exists
				if (findTreeItemPackage(tc.getPackageName()) != null) {
					// java file already exists
					if (findTreeItemJavaFile(tc.getJavaFile()) != null) {
						log.debug("Java File node already exists");
						parentNodeItem = findTreeItemJavaFile(tc.getJavaFile());
						TreeItem testCaseItem = new TreeItem(parentNodeItem, 0);
						testCaseItem.setText(tc.getName());
						testCaseItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/test.png")));
					}
					// java file doesn't exists
					else {
						log.debug("Java File doesn't exist");
						parentNodeItem = findTreeItemPackage(tc.getPackageName());
						TreeItem javaFileItem = new TreeItem(parentNodeItem, 0);
						javaFileItem.setText(tc.getJavaFile());
						javaFileItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/tsuite.png")));
						TreeItem testCaseItem = new TreeItem(javaFileItem, 0);
						testCaseItem.setText(tc.getName());
						testCaseItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/test.png")));
					}
				}
				// package doesn't exist
				else {
					log.debug("package doesn't exists");
					parentNodeItem = findTreeItemSuite(tc.getTestSuite());
					TreeItem packageItem = new TreeItem(parentNodeItem, 0);
					packageItem.setText(tc.getPackageName());
					packageItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/tsuite.png")));
					TreeItem javaFileItem = new TreeItem(packageItem, 0);
					javaFileItem.setText(tc.getJavaFile());
					javaFileItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/tsuite.png")));
					TreeItem testCaseItem = new TreeItem(javaFileItem, 0);
					testCaseItem.setText(tc.getName());
					testCaseItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/test.png")));
				}
				// test suite doesn't exist
			} else {
				log.debug("Test suite node doesn't exist");
				TreeItem suiteItem = new TreeItem(TRView.getTree(), 0);
				suiteItem.setText(tc.getTestSuite());
				suiteItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/tsuite.png")));
				TreeItem packageItem = new TreeItem(suiteItem, 0);
				packageItem.setText(tc.getPackageName());
				packageItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/tsuite.png")));
				TreeItem javaFileItem = new TreeItem(packageItem, 0);
				javaFileItem.setText(tc.getJavaFile());
				javaFileItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/tsuite.png")));
				TreeItem testCaseItem = new TreeItem(javaFileItem, 0);
				testCaseItem.setText(tc.getName());
				testCaseItem.setImage(new Image(TRView.getDisplay(), TRView.class.getResourceAsStream("/test.png")));
			}
		}
	}

	private static TreeItem findTreeItemSuite(String testSuiteName) {
		for (TreeItem suiteNode : TRView.getTree().getItems()) {
			if (suiteNode.getText().equals(testSuiteName)) {
				return suiteNode;
			}
		}
		return null;
	}

	private static TreeItem findTreeItemPackage(String packageName) {
		for (TreeItem suiteNode : TRView.getTree().getItems()) {
			for (TreeItem packageNode : suiteNode.getItems()) {
				if (packageNode.getText().equals(packageName)) {
					return packageNode;
				}
			}
		}
		return null;
	}

	private static TreeItem findTreeItemJavaFile(String javaFileName) {
		for (TreeItem suiteNode : TRView.getTree().getItems()) {
			for (TreeItem packageNode : suiteNode.getItems()) {
				for (TreeItem javaFileNode : packageNode.getItems()) {
					if (javaFileNode.getText().equals(javaFileName)) {
						return javaFileNode;
					}
				}
			}
		}
		return null;
	}

	/**
	 * TODO: Comment
	 *
	 * @param testCase
	 * @param expandNodes
	 * @return
	 */
	private static TreeItem findTreeItemCaseName(StringTestCase testCase, Boolean expandNodes) {
		for (TreeItem suiteNode : TRView.getTree().getItems()) {
			if (suiteNode.getText().equals(testCase.getTestSuite())) {
				suiteNode.setExpanded(true);
				for (TreeItem packageNode : suiteNode.getItems()) {
					if (packageNode.getText().equals(testCase.getPackageName())) {
						packageNode.setExpanded(true);
						for (TreeItem javaFileNode : packageNode.getItems()) {
							if (javaFileNode.getText().equals(testCase.getJavaFile())) {
								javaFileNode.setExpanded(true);
								for (TreeItem testCaseNode : javaFileNode.getItems()) {
									if (testCaseNode.getText().equals(testCase.getName())) {
										return testCaseNode;
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
}
