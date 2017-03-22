package com.mcoufal.inrunjunit.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.jboss.reddeer.common.logging.Logger;

import com.mcoufal.inrunjunit.listener.JUnitListenerEP;

/**
 * TODO: comments, getters
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 *
 */
public class StringTestRunSession implements Serializable {
	// set up logger
	private final static Logger log = Logger.getLogger(StringTestRunSession.class);

	private String testRunName;
	private double elapsedTime;
	private String failureTrace;
	private List<StringTestElement> childrenElements = new ArrayList<StringTestElement>();
	
	/**
	 * TODO: initialize + comments + handle nulls
	 * @param session
	 */
	public StringTestRunSession(ITestRunSession session) {
		// TODO: convert session to string representation!
		testRunName = session.getTestRunName();
		elapsedTime = session.getElapsedTimeInSeconds();
		failureTrace = session.getFailureTrace() != null ? session.getFailureTrace().getActual() : null;
		// TODO: what about Result? Already have StringResult class
		// handle children
		for (ITestElement el : session.getChildren()){
			childrenElements.add(new StringTestElement(el));
		}
	}

	/**
	 * TODO: handle nulls! Results print with StringResult
	 * @param session
	 */
	public static void printSession(ITestRunSession session) {
		System.out.println("Session: " + session.toString());
		System.out.println("Test Run Name: " + session.getTestRunName());
		System.out.println("Elapsed Time: " + session.getElapsedTimeInSeconds() + "s");
		System.out.println("<Failure Trace>: " + session.getFailureTrace());
		System.out.println("<Launched Project>: " + session.getLaunchedProject());
		System.out.println("<Parent Container>: " + session.getParentContainer());
		System.out.println("<Progress State>: " + session.getProgressState());
		System.out.println("<Test Result>(with children): " + session.getTestResult(true).toString());
		System.out.println("<Test Result>(without children): " + session.getTestResult(false).toString());
		System.out.println("<Test Run Session>: " + session.getTestRunSession().toString());
		System.out.println("-Children-");
		for (ITestElement el : session.getChildren()){
			System.out.println("<Test Element>: " + el.toString());
		}
		
	}
}
