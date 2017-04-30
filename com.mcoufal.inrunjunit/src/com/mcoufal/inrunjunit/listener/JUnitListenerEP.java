package com.mcoufal.inrunjunit.listener;

import java.io.IOException;

import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.jboss.reddeer.common.logging.Logger;

import com.mcoufal.inrunjunit.server.ResultsServer;

/**
 * This class extends 'org.eclipse.jdt.junit.TestRunListener' and contributes to
 * 'org.eclipse.jdt.junit.testRunListeners' extension point. JUnitListenerEP is
 * automatically invoked by this extension point. JUnitListenerEP listens to
 * JUnit test sessions and sends test results data to ResultsServer instance.
 * When created, JUnitListenerEP initializes ResultsServer thread that is used
 * to send data from listener to TRView application.
 *
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 */
public class JUnitListenerEP extends TestRunListener {
	// set up logger
	private final static Logger log = Logger.getLogger(JUnitListenerEP.class);
	// Results server thread
	public ResultsServer server;

	// phases that listener recognizes
	public enum Phase {
		SESSION_LAUNCHED, SESSION_STARTED, SESSION_FINISHED, CASE_STARTED, CASE_FINISHED
	}

	/**
	 * Constructor. Initializes and starts ResultsServer thread.
	 * 
	 * TODO: add 'debug' option: tests can't start before TRView is connected
	 * (pause this thread probably) - will need some *global* variable
	 */
	public JUnitListenerEP() {
		log.info("JUnitListenerEP created");
		// ResultsServer is handled by new thread
		server = new ResultsServer();
		server.start();
		log.info("JUnitListenerEP initialized");
	}

	/**
	 * This method is invoked when test run session launches. Sends data to
	 * ResultsServer instance.
	 */
	@Override
	public void sessionLaunched(ITestRunSession session) {
		log.info("Session '" + session.getTestRunName() + "' LAUNCHED");
		server.sendData(session, Phase.SESSION_LAUNCHED);
	}

	/**
	 * This method is invoked when test run session starts. Sends data to
	 * ResultsServer instance.
	 */
	@Override
	public void sessionStarted(ITestRunSession session) {
		log.info("Session '" + session.getTestRunName() + "' STARTED");
		server.sendData(session, Phase.SESSION_STARTED);
	}

	/**
	 * This method is invoked when test run session finishes. Sends data to
	 * ResultsServer instance.
	 */
	@Override
	public void sessionFinished(ITestRunSession session) {
		log.info("Session '" + session.getTestRunName() + "' FINISHED");
		server.sendData(session, Phase.SESSION_FINISHED);
		try {
			server.getServSock().close();
		} catch (IOException e) {
			log.error("Error while initializing ResultsServer end: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * This method is invoked when test case starts. Sends data to
	 * ResultsServer instance.
	 */
	@Override
	public void testCaseStarted(ITestCaseElement testCaseElement) {
		log.info("Test case '" + testCaseElement.getTestMethodName() + "' STARTED");
		server.sendData(testCaseElement, Phase.CASE_STARTED);
	}

	/**
	 * This method is invoked when test case finishes. Sends data to
	 * ResultsServer instance.
	 */
	@Override
	public void testCaseFinished(ITestCaseElement testCaseElement) {
		log.info("Test case '" + testCaseElement.getTestMethodName() + "' FINISHED");
		server.sendData(testCaseElement, Phase.CASE_FINISHED);
	}
}
