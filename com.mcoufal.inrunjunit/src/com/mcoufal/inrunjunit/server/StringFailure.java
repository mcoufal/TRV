package com.mcoufal.inrunjunit.server;

import org.jboss.reddeer.common.logging.Logger;
import org.junit.runner.notification.Failure;

public class StringFailure {
	// set up logger
	private final static Logger log = Logger.getLogger(StringFailure.class);
	
	// String representations of Failure data
	private static StringDescription failureStringDescription;
	
	public StringFailure(Failure fail) {
		//TODO: initialize variables
		failureStringDescription = new StringDescription(fail.getDescription());
		// TODO: string exception class!
		
		
		
		// printFailure(fail);
	}

	/**
	 * Prints Failure attributes. TODO: remove when done.
	 * 
	 * @param failure
	 */
	private static void printFailure(Failure failure) {
		log.info("Printing failure description");
		System.out.println("Failure: " + failure.toString());
		System.out.println("<Description>: " + failure.getDescription().toString());
		System.out.println("<Exception>: " + failure.getException().getMessage());
		System.out.println("Message: " + failure.getMessage());
		System.out.println("Test Header: " + failure.getTestHeader());
		System.out.println("Trace: " + failure.getTrace());
	}
}
