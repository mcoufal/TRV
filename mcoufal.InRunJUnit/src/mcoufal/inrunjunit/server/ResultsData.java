package mcoufal.inrunjunit.server;

import java.io.Serializable;

import mcoufal.inrunjunit.listener.JUnitListenerEP.Phase;

/**
 * This class is used to group test data from JUnitListenerEP and create string
 * representations of this data. Results data implements Serializable interface
 * in order to make sending via sockets simple.
 * 
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 */
public class ResultsData implements Serializable {
	// serial ID used for serialization
	private static final long serialVersionUID = 1L;
	// phase where data were caught
	private Phase phase;
	// data for different phases
	private StringTestCaseElement testCaseElement;
	private StringTestRunSession testRunSession;

	/* Constructors */

	/**
	 * Constructor for ResultsData created from string representation of
	 * ITestCaseElement instances and Phase.
	 * 
	 * @param desc
	 * @param phase
	 */
	public ResultsData(StringTestCaseElement testCaseElement, Phase phase) {
		setTestRunSession(null);
		setTestCaseElement(testCaseElement);
		setPhase(phase);
	}

	/**
	 * Constructor for ResultsData created from string representation of
	 * ITestRunSession instances and Phase.
	 * 
	 * @param result
	 * @param phase
	 */
	public ResultsData(StringTestRunSession testRunSession, Phase phase) {
		setTestRunSession(testRunSession);
		setTestCaseElement(null);
		setPhase(phase);
	}

	/* Get and Set methods */

	/**
	 * @return current phase
	 */
	public Phase getPhase() {
		return phase;
	}

	/**
	 * Sets current phase
	 * 
	 * @param phase
	 */
	private void setPhase(Phase phase) {
		this.phase = phase;
	}

	/**
	 * @return TestCaseElement of current phase
	 */
	public StringTestCaseElement getTestCaseElement() {
		return testCaseElement;
	}

	/**
	 * Sets TestCaseElement of current phase
	 * 
	 * @param desc
	 */
	private void setTestCaseElement(StringTestCaseElement testCaseElement) {
		this.testCaseElement = testCaseElement;
	}

	/**
	 * @return TestRunSession of current phase
	 */
	public StringTestRunSession getTestRunSession() {
		return testRunSession;
	}

	/**
	 * Sets TestRunSession of current phase
	 * 
	 * @param result
	 */
	private void setTestRunSession(StringTestRunSession testRunSession) {
		this.testRunSession = testRunSession;
	}
}
