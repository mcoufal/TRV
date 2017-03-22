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
	
	/**
	 * initialize, handle nulls, comments
	 * @param testCaseElement
	 */
	public StringTestCaseElement(ITestCaseElement testCaseElement) {
		// TODO: convert session to string representation
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
