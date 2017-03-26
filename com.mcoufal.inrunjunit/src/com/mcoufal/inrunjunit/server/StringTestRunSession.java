package com.mcoufal.inrunjunit.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
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

	private String testRunSession;
	private String testRunName;
	private double elapsedTime;
	private String failureTrace;
	private List<StringTestElement> childrenElements = new ArrayList<StringTestElement>();
	private StringResult testResultNoChildren;
	private StringResult testResultWithChildren;
	private String launchedProject;
	private String parentContainer;
	private String progressState;
	
	/**
	 * TODO: initialize + comments + handle nulls
	 * @param session
	 */
	public StringTestRunSession(ITestRunSession session) {
		// TODO: convert session to string representation!
		if (session != null) testRunSession = session.toString();
		else testRunSession = null;
		testRunName = session.getTestRunName();
		elapsedTime = session.getElapsedTimeInSeconds();
		failureTrace = session.getFailureTrace() != null ? session.getFailureTrace().getActual() : null;
		testResultNoChildren = new StringResult(session.getTestResult(false));
		testResultWithChildren = new StringResult(session.getTestResult(true));
		if (session.getLaunchedProject() != null) launchedProject = session.getLaunchedProject().toString();
		else launchedProject = null;
		if (session.getParentContainer() != null) parentContainer = session.getParentContainer().toString();
		else parentContainer = null;
		if (session.getProgressState() != null) progressState = session.getProgressState().toString();
		else progressState = null;
		// handle children
		for (ITestElement el : session.getChildren()){
			childrenElements.add(new StringTestElement(el));
		}
		
		/*SPACE FOR TESTING*/
		try {
			log.debug("TESTING:");
			//for (ITestElement el : session.getChildren()){
				log.debug("ITestElement: " + session.getParentContainer().toString());
				log.debug("First level: " + session.getTestRunName());
				log.debug("Second level: " + session.getTestRunSession().getTestRunName());
				log.debug("Third level: " + session.getTestRunSession().getTestRunSession().getTestRunName());
				log.debug("Fourth level: " + session.getTestRunSession().getTestRunSession().getTestRunSession().getTestRunName());
				/*log.debug("ITestElement-getLaunchedProject children: ");
				for (IJavaElement grandchild : el.getTestRunSession().getLaunchedProject().getChildren()){
					log.debug("GrandChild resource: " + grandchild.getResource());
				}
				log.debug("ITestElement-getTestRunSession: " + el.getTestRunSession().getTestRunName());
				for (ITestElement child : el.getTestRunSession().getChildren()){
					log.debug("CHILD: " + child);
				}*/
			//}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Something went wrong in TESTING phase of InRunJunit:\n");
			e.printStackTrace();
		}
		/**/
	}
	
	public void print(){
		System.out.println("[testRunSession]: " + testRunSession);
		System.out.println("[testRunName]: " + testRunName);
		System.out.println("[elapsedTime]: " + elapsedTime);
		System.out.println("[failureTrace]: " + failureTrace);
		testResultNoChildren.print();
		testResultWithChildren.print();
		System.out.println("[launchedProject]: " + launchedProject);
		System.out.println("[parentContainer]: " + parentContainer);
		System.out.println("[progressState]: " + progressState);
		for (StringTestElement element : childrenElements){
			element.print();
		}
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
	 * @return the children elements
	 */
	public List<StringTestElement> getChildrenElements() {
		return childrenElements;
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
	 * @return the string representation of launched project
	 */
	public String getLaunchedProject() {
		return launchedProject;
	}

	/**
	 * @return the string representation of parent container
	 */
	public String getParentContainer() {
		return parentContainer;
	}

	/**
	 * @return the string representation of progress state
	 */
	public String getProgressState() {
		return progressState;
	}

	/**
	 * TODO: handle nulls! Results print with StringResult
	 * @param session
	 */
	/*public static void printSession(ITestRunSession session) {
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
	}*/
}
