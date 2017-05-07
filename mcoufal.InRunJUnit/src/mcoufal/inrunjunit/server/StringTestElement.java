package mcoufal.inrunjunit.server;

import java.io.Serializable;

import org.eclipse.jdt.junit.model.ITestElement;

/**
 * Creates StringTestElement instance containing string data of ITestElement object.
 * 
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 *
 */
@SuppressWarnings("serial")
public class StringTestElement implements Serializable {
	// define variables
	private static String testElement;
	private static double elapsedTime;
	private static String failureTrace;
	private static String parentContainer;
	private static String progressState;
	private static StringResult testResultNoChildren;
	private static StringResult testResultWithChildren;
	private static String testRunSession;
	
	/**
	 * Creates StringTestElement instance containing string data of ITestElement object.
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
		testRunSession = element.getTestRunSession().toString();
	}
	
	/**
	 * Prints all available StringTestElement data to stdout.
	 */
	public void print(){
		System.out.println("[testElement]: " + testElement);
		System.out.println("[elapsedTime]: " + elapsedTime);
		System.out.println("[failureTrace]: " + failureTrace);
		System.out.println("[parentContainer]: " + parentContainer);
		System.out.println("[progressState]: " + progressState);
		if (testResultNoChildren != null) testResultNoChildren.print();
		if (testResultWithChildren != null) testResultWithChildren.print();
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
}
