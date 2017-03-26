package com.mcoufal.inrunjunit.server;

import java.io.Serializable;

import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.jboss.reddeer.common.logging.Logger;

/**
 * TODO: constructor, print_element fn(), comments, getters
 * 
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 *
 */
public class StringTestElement implements Serializable {
	// set up logger
	private final static Logger log = Logger.getLogger(StringTestElement.class);
	// define variables
	private static String testElement;
	private static double elapsedTime;
	private static String failureTrace;
	private static String parentContainer;
	private static String progressState;
	private static StringResult testResultNoChildren;
	private static StringResult testResultWithChildren;
	//private static StringTestRunSession testRunSession;
	private static String testRunSession;
	
	/**
	 * TODO: comment
	 * 
	 * @param element
	 */
	public StringTestElement(ITestElement element) {
		// initialize
		if (element != null) testElement = element.toString();
		else testElement = null;
		elapsedTime = element.getElapsedTimeInSeconds();
		if (element.getFailureTrace() != null) failureTrace = element.getFailureTrace().toString();
		else failureTrace = null;
		if (element.getParentContainer() != null) parentContainer = element.getParentContainer().toString();
		else parentContainer = null;
		if (element.getProgressState() != null) progressState = element.getProgressState().toString();
		else progressState = null;
		testResultNoChildren = new StringResult(element.getTestResult(false));
		testResultWithChildren = new StringResult(element.getTestResult(true));
		//testRunSession = new StringTestRunSession(element.getTestRunSession());
		testRunSession = element.getTestRunSession().toString();
	}
	
	/**
	 * TODO
	 */
	public void print(){
		System.out.println("[testElement]: " + testElement);
		System.out.println("[elapsedTime]: " + elapsedTime);
		System.out.println("[failureTrace]: " + failureTrace);
		System.out.println("[parentContainer]: " + parentContainer);
		System.out.println("[progressState]: " + progressState);
		if (testResultNoChildren != null) testResultNoChildren.print();
		if (testResultWithChildren != null) testResultWithChildren.print();
		//if (testRunSession != null) testRunSession.print();
		System.out.println("[testRunSession]: " + testRunSession);
	}

	/**
	 * @return the testElement
	 */
	public String getTestElement() {
		return testElement;
	}

	/**
	 * @return the elapsedTime
	 */
	public double getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * @return the failureTrace
	 */
	public String getFailureTrace() {
		return failureTrace;
	}

	/**
	 * @return the parentContainer
	 */
	public String getParentContainer() {
		return parentContainer;
	}

	/**
	 * @return the progressState
	 */
	public String getProgressState() {
		return progressState;
	}

	/**
	 * @return the testResultNoChildren
	 */
	public StringResult getTestResultNoChildren() {
		return testResultNoChildren;
	}

	/**
	 * @return the testResultWithChildren
	 */
	public StringResult getTestResultWithChildren() {
		return testResultWithChildren;
	}

	/**
	 * @return the testRunSession
	 */
	public String getTestRunSession() {
		return testRunSession;
	}

	/**
	 * TODO: delete when done
	 * 
	 * @param element
	 */
	public static void printElement(ITestElement element) {
		log.info("TestElement: '" + element + "'");
		log.info("ElapsedTime: '" + element.getElapsedTimeInSeconds() + "'");
		log.info("FailureTrace: '" + element.getFailureTrace() + "'");
		// log.info("" + element.get + "'");
		// log.info("" + element.toString() + "'");
		// log.info("" + element.toString() + "'");
	}
}
