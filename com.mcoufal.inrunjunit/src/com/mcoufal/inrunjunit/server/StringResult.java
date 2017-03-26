package com.mcoufal.inrunjunit.server;

import java.io.Serializable;

import org.jboss.reddeer.common.logging.Logger;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * TODO
 *
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 */
public class StringResult implements Serializable {
	// set up logger
	private final static Logger log = Logger.getLogger(StringResult.class);
	private String status;
	
	public StringResult(org.eclipse.jdt.junit.model.ITestElement.Result result) {
		if (result != null)
			status = result.toString();
		else status = "Undefined";
	}

	/**
	 * TODO
	 */
	public void print(){
		System.out.println("[result status]: " + status);
	}
	/**
	 * Prints Result attributes. TODO: remove when done.
	 * 
	 * @param result
	 */
	/*private static void printResult(Result result) {
		log.info("Printing result description");
		System.out.println("Result: " + result.toString());
		System.out.println("Failure Count: " + result.getFailureCount());
		System.out.println("Ignore Count: " + result.getIgnoreCount());
		System.out.println("Run Count: " + result.getRunCount());
		System.out.println("Run Time: " + result.getRunTime());
		System.out.println("Successfull?: " + (result.wasSuccessful() ? "YES" : "NO"));
		System.out.println("-Failures-");
		for (Failure fail : result.getFailures()) {
			System.out.println("<Failure>: " + fail.getMessage());
		}
	}*/
}
