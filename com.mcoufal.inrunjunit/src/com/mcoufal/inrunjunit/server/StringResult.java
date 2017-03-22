package com.mcoufal.inrunjunit.server;

import org.jboss.reddeer.common.logging.Logger;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class StringResult {
	// set up logger
	private final static Logger log = Logger.getLogger(StringResult.class);

	public StringResult(Result res) {
		//TODO: initialize variables
		
		// printResult(res);
	}

	/**
	 * Prints Result attributes. TODO: remove when done.
	 * 
	 * @param result
	 */
	private static void printResult(Result result) {
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
	}
}
