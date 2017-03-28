package com.mcoufal.inrunjunit.server;

import java.io.Serializable;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.jboss.reddeer.common.logging.Logger;

import com.mcoufal.inrunjunit.listener.JUnitListenerEP;

/**
 * TODO: comments, getters
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 *
 */
public class StringTestCaseElement implements Serializable {
	// set up logger
	private final static Logger log = Logger.getLogger(StringTestCaseElement.class);
	private String testCaseElement;
	private String testClassName;
	private String testMethodName;
	private Double elapsedTime;
	private String failureTrace;
	private String parentContainer;
	private String progressState;
	private StringResult testResultWithChildren;
	private StringResult testResultNoChildren;
	private String testRunSession;
	
	/**
	 * initialize, handle nulls, comments
	 * @param testCaseElement
	 */
	public StringTestCaseElement(ITestCaseElement testCaseElement) {
		if (testCaseElement.toString() != null) this.testCaseElement = testCaseElement.toString();
		else this.testCaseElement = null;
		testClassName = testCaseElement.getTestClassName();
		testMethodName = testCaseElement.getTestMethodName();
		elapsedTime = testCaseElement.getElapsedTimeInSeconds();
		if (testCaseElement.getFailureTrace() != null) failureTrace = testCaseElement.getFailureTrace().toString();
		else failureTrace = null;
		if (testCaseElement.getParentContainer() != null) parentContainer = testCaseElement.getParentContainer().toString();
		else parentContainer = null;
		if (testCaseElement.getProgressState() != null) progressState = testCaseElement.getProgressState().toString();
		else progressState = null;
		testResultWithChildren = new StringResult(testCaseElement.getTestResult(true));
		testResultNoChildren = new StringResult(testCaseElement.getTestResult(false));
		if (testCaseElement.getTestRunSession() != null) testRunSession = testCaseElement.getTestRunSession().toString();
		else testRunSession = null;
	}
	
	/**
	 * TODO
	 */
	public void print(){
		System.out.println("[testCaseElement]" + testCaseElement);
		System.out.println("[testClassName]" + testClassName);
		System.out.println("[testMethodName]" + testMethodName);
		System.out.println("[elapsedTime]" + elapsedTime);
		System.out.println("[failureTrace]" + failureTrace);
		System.out.println("[parentContainer]" + parentContainer);
		System.out.println("[progressState]" + progressState);
		System.out.println("[testResultWithChildren]" + testResultWithChildren);
		System.out.println("[testResultNoChildren]" + testResultNoChildren);
		System.out.println("[testRunSession]" + testRunSession);
	}

	public static void printCaseElement(ITestCaseElement testCaseElement) {
		System.out.println("Test Case Element: " + testCaseElement.toString());
		System.out.println("Test Class Name: " + testCaseElement.getTestClassName());
		System.out.println("Test Method Name: " + testCaseElement.getTestMethodName());
		System.out.println("Elapsed Time: " + testCaseElement.getElapsedTimeInSeconds() + "s");
		//System.out.println("<Failure Trace>: " + testCaseElement.getFailureTrace().toString());
		//System.out.println("<Parent Container>: " + testCaseElement.getParentContainer().toString());
		//System.out.println("<Progress State>: " + testCaseElement.getProgressState().toString());
		System.out.println("<Test Result>(with children): " + testCaseElement.getTestResult(true).toString());
		System.out.println("<Test Result>(without children): " + testCaseElement.getTestResult(false).toString());
		System.out.println("<Test Run Session>: " + testCaseElement.getTestRunSession().toString());
	}
}
