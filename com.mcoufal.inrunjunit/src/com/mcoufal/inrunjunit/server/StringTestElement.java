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

	/**
	 * TODO: comments, handle null!?
	 * 
	 * @param element
	 */
	public StringTestElement(ITestElement element) {
		// TODO: convert ITestElement to string representation + initialize!
		testElement = element.toString();
		elapsedTime = element.getElapsedTimeInSeconds();
		// failureTrace = element.getFailureTrace().toString();

	}

	/**
	 * TODO: comment + finish converting variables to string representations
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
