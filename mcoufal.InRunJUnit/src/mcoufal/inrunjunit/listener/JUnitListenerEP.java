package mcoufal.inrunjunit.listener;

import java.io.IOException;

import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestRunSession;

import mcoufal.inrunjunit.server.ResultsServer;

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
	// Results server thread
	public static ResultsServer server;

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
		// TODO: here is space to make DEBUG mode!
	}

	/**
	 * This method is invoked when test run session launches. Sends data to
	 * ResultsServer instance.
	 */
	@Override
	public void sessionLaunched(ITestRunSession session) {
		// ResultsServer is handled by new thread
		server = new ResultsServer();
		server.start();
		server.sendData(session, Phase.SESSION_LAUNCHED);
	}

	/**
	 * This method is invoked when test run session starts. Sends data to
	 * ResultsServer instance.
	 */
	@Override
	public void sessionStarted(ITestRunSession session) {
		server.sendData(session, Phase.SESSION_STARTED);
	}

	/**
	 * This method is invoked when test run session finishes. Sends data to
	 * ResultsServer instance.
	 */
	@Override
	public void sessionFinished(ITestRunSession session) {
		server.sendData(session, Phase.SESSION_FINISHED);
		try {
			server.getServSock().close();
		} catch (IOException e) {
			System.err.println("Error while initializing ResultsServer end: " + e.getMessage());
			e.printStackTrace();
		}
		server = null;
	}

	/**
	 * This method is invoked when test case starts. Sends data to ResultsServer
	 * instance.
	 */
	@Override
	public void testCaseStarted(ITestCaseElement testCaseElement) {
		server.sendData(testCaseElement, Phase.CASE_STARTED);
	}

	/**
	 * This method is invoked when test case finishes. Sends data to
	 * ResultsServer instance.
	 */
	@Override
	public void testCaseFinished(ITestCaseElement testCaseElement) {
		server.sendData(testCaseElement, Phase.CASE_FINISHED);
	}
}
