package view;

import java.util.List;

import org.eclipse.swt.widgets.TreeItem;
import org.jboss.reddeer.common.logging.Logger;

import com.mcoufal.inrunjunit.server.ResultsData;
import com.mcoufal.inrunjunit.server.StringTestCaseElement;
import com.mcoufal.inrunjunit.server.StringTestElement;
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
	}

	/**
	 * This method parses StringTestCaseElement received in CASE_FINISHED phase
	 * and displays results in TRView according to the phase.
	 * TODO
	 * @param testCaseElement
	 */
	private static void parseAndDisplayCaseFinished(StringTestCaseElement testCaseElement) {
		// TODO Auto-generated method stub
		log.info("parsing CASE FINISHED - availible data:");
		testCaseElement.print();

	}

	/**
	 * This method parses StringTestCaseElement received in CASE_STARTED phase
	 * and displays results in TRView according to the phase.
	 * TODO
	 * @param testCaseElement
	 */
	private static void parseAndDisplayCaseStarted(StringTestCaseElement testCaseElement) {
		// TODO Auto-generated method stub
		log.info("parsing CASE STARTED - availible data:");
		testCaseElement.print();
	}

	/**
	 * This method parses StringTestRunSession received in SESSION_FINISHED
	 * phase and displays results in TRView according to the phase.
	 * TODO
	 * @param testRunSession
	 */
	private static void parseAndDisplaySessionFinished(StringTestRunSession testRunSession) {
		// TODO Auto-generated method stub
		log.info("parsing SESSION FINISHED - availible data:");
		testRunSession.print();

	}

	/**
	 * This method parses StringTestRunSession received in SESSION_STARTED phase
	 * and displays results in TRView according to the phase.
	 * TODO
	 * @param testRunSession
	 */
	private static void parseAndDisplaySessionStarted(StringTestRunSession testRunSession) {
		// TODO Auto-generated method stub
		log.info("parsing SESSION STARTED - availible data:");
		testRunSession.print();

		// local variables
		int numberOfRuns = testRunSession.getChildrenElements().size();
		log.debug("numberOfChildren: " + numberOfRuns);

		// parsing
		TRView.getLblRuns().setText("0/" + numberOfRuns);
		TreeItem t1 = new TreeItem(TRView.getTree(), 0);
		t1.setText(testRunSession.getTestRunName());
		for (StringTestElement element : testRunSession.getChildrenElements()) {
			TreeItem t2 = new TreeItem(t1, 0);
			t2.setText(element.getTestElement());
		}

	}

	/**
	 * This method parses StringTestRunSession received in SESSION_LAUNCHED
	 * phase and displays results in TRView according to the phase.
	 * TODO
	 * @param testRunSession
	 */
	private static void parseAndDisplaySessionLaunched(StringTestRunSession testRunSession) {
		// TODO Auto-generated method stub
		log.info("parsing SESSION LAUNCHED - availible data:");
		testRunSession.print();
		// local variables
		int numberOfRuns = testRunSession.getChildrenElements().size();
		log.debug("numberOfChildren: " + numberOfRuns);

		// parsing
		/*TRView.getLblRuns().setText("0/" + numberOfRuns);
		TreeItem t1 = new TreeItem(TRView.getTree(), 0);
		t1.setText(testRunSession.getTestRunName());
		for (StringTestElement element : testRunSession.getChildrenElements()) {
			TreeItem t2 = new TreeItem(t1, 0);
			t2.setText(element.getTestElement());
		}*/
	}

}
