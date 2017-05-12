package mcoufal.inrunjunit.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.junit.model.ITestRunSession;

/**
 * Creates StringTestRunSession instance containing string data of
 * ITestRunSession object.
 *
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 *
 */
@SuppressWarnings("serial")
public class StringTestRunSession implements Serializable {
	private String testRunSession;
	private String testRunName;
	private double elapsedTime;
	private String failureTrace;
	private StringResult testResultNoChildren;
	private StringResult testResultWithChildren;
	// test case
	private List<StringTestCase> testCases = new ArrayList<StringTestCase>();


	/**
	 * Creates StringTestRunSession instance containing string data of
	 * ITestRunSession object.
	 *
	 * @param session
	 */
	public StringTestRunSession(ITestRunSession session, Boolean closeResources) {
		if (session != null)
			testRunSession = session.toString();
		else
			testRunSession = null;

		int helpIndex = session.getTestRunName().indexOf(' ');
		if (helpIndex > 0)
			testRunName = session.getTestRunName().substring(0, helpIndex);
		else
			testRunName = session.getTestRunName();
		elapsedTime = session.getElapsedTimeInSeconds();
		failureTrace = session.getFailureTrace() != null ? session.getFailureTrace().getActual() : null;
		testResultNoChildren = new StringResult(session.getTestResult(false));
		testResultWithChildren = new StringResult(session.getTestResult(true));
		testCases = (new TestRunSessionParser(session, closeResources)).getTestCases();
	}

	/**
	 * TODO not complete
	 */
	public void print() {
		System.out.println("[testRunSession]: " + testRunSession);
		System.out.println("[testRunName]: " + testRunName);
		System.out.println("[elapsedTime]: " + elapsedTime);
		System.out.println("[failureTrace]: " + failureTrace);
		testResultNoChildren.print();
		testResultWithChildren.print();
	}

	/**
	 * @return the string representation of ITestRunSession
	 */
	public String getTestRunSession() {
		return testRunSession;
	}

	/**
	 * @return the test run name
	 */
	public String getTestRunName() {
		return testRunName;
	}

	/**
	 * @return the elapsed time
	 */
	public double getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * @return the failure trace
	 */
	public String getFailureTrace() {
		return failureTrace;
	}

	/**
	 * @return the test result without children considered
	 */
	public StringResult getTestResultNoChildren() {
		return testResultNoChildren;
	}

	/**
	 * @return the test result with children considered
	 */
	public StringResult getTestResultWithChildren() {
		return testResultWithChildren;
	}

	/**
	 * @return list of test cases information
	 */
	public List<StringTestCase> getTestCases() {
		return testCases;
	}
}
