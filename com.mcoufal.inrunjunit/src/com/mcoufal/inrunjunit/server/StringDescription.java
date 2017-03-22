package com.mcoufal.inrunjunit.server;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.jboss.reddeer.common.logging.Logger;
import org.junit.runner.Description;

public class StringDescription {
	// set up logger
	private final static Logger log = Logger.getLogger(StringDescription.class);
	
	// String representations of description data
	private static String sMethodName;
	private static String sClassName;
	private static String sDisplayName;
	private static Boolean bIsEmpty;
	private static Boolean bIsSuite;
	private static Boolean bIsTest;
	private static int testCount;
	private static List<StringDescription> childrenDescription = new ArrayList<StringDescription>();
	//TODO: finish others
	
	public StringDescription(Description desc) {
		//TODO: initialize variables!
		sMethodName = desc.getMethodName();
		sClassName = desc.getClassName();
		sDisplayName = desc.getDisplayName();
		bIsEmpty = desc.isEmpty();
		bIsSuite = desc.isSuite();
		bIsTest = desc.isTest();
		testCount = desc.testCount();
		// TODO: do I need sth from description.getTestClass()?
		// TODO: do I need annotations? (description.getAnnotations())
		for (Description childDesc : desc.getChildren()){
			childrenDescription.add(new StringDescription(childDesc));
		}
		// see what is in description
		// printDesc(desc);
	}

	/**/
	
	/**
	 * Prints Description attributes. TODO: remove when done.
	 * 
	 * @param description
	 */
	private static void printDesc(Description description) {
		log.info("Printing test description");
		System.out.println("Description: " + description.toString());
		System.out.println("Is Empty? : " + (description.isEmpty() ? "YES" : "NO"));
		System.out.println("Is Suite? :" + (description.isSuite() ? "YES" : "NO"));
		System.out.println("Is Test?: " + (description.isTest() ? "YES" : "NO"));
		System.out.println("Test Count: " + description.testCount());
		System.out.println("Class Name: " + description.getClassName());
		System.out.println("Display Name : " + description.getDisplayName());
		System.out.println("Method Name: " + description.getMethodName());
		System.out.println("<Test Class>: " + description.getTestClass().getName());

		System.out.println("-Annotations-");
		for (Annotation annotation : description.getAnnotations()) {
			System.out.println("<Annotation>: " + annotation.toString());
		}
		System.out.println("-Children's description-");
		for (Description desc : description.getChildren()) {
			System.out.println("<Description>: " + desc.getClassName());
		}
	}
}
