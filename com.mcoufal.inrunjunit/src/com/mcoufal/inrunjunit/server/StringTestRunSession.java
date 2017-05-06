package com.mcoufal.inrunjunit.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.jboss.reddeer.common.logging.Logger;

/**
 * Creates StringTestRunSession instance containing string data of
 * ITestRunSession object.
 * 
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 *
 */
@SuppressWarnings("serial")
public class StringTestRunSession implements Serializable {
	// set up logger
	private final static Logger log = Logger.getLogger(StringTestRunSession.class);
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
	public StringTestRunSession(ITestRunSession session) {
		if (session != null)
			testRunSession = session.toString();
		else
			testRunSession = null;
		// FIXME: index out of range when running tests from package node
		testRunName = session.getTestRunName().substring(0, session.getTestRunName().indexOf(' '));
		elapsedTime = session.getElapsedTimeInSeconds();
		failureTrace = session.getFailureTrace() != null ? session.getFailureTrace().getActual() : null;
		testResultNoChildren = new StringResult(session.getTestResult(false));
		testResultWithChildren = new StringResult(session.getTestResult(true));
		testCases = (new TestRunSessionParser(session)).getTestCases();
		log.debug("PATH of project: " + session.getLaunchedProject().getProject().getFullPath().toOSString());
		log.debug("testRunSession:" + testRunSession);
		log.debug("testRunName:" + testRunName);
		log.debug("getElementName: " + session.getLaunchedProject().getElementName());
		log.debug("WORKSPACE PATH: " + System.getProperty("user.dir"));
		try {
			log.debug("PATH?: " + session.getLaunchedProject().getJavaProject().getOutputLocation());
		} catch (JavaModelException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		// log.debug(session.getLaunchedProject().getProject().getWorkingLocation());

		log.debug("getTestRunSession.getTestRunName: " + session.getTestRunSession().getTestRunName());
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
