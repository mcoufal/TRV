package com.mcoufal.inrunjunit.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.jboss.reddeer.common.logging.Logger;

/**
 * Creates StringTestRunSession instance containing string data of
 * ITestRunSession object.
 * 
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 *
 */
@SuppressWarnings("serial")
public class StringTestRunSession implements Serializable {
	// set up logger
	private final static Logger log = Logger.getLogger(StringTestRunSession.class);
	private String testRunSession;
	private String testRunName;
	private double elapsedTime;
	private String failureTrace;
	private StringResult testResultNoChildren;
	private StringResult testResultWithChildren;
	// test case
	private List<StringTestCase> testCases = new ArrayList<StringTestCase>();
	private String sTestCaseName = null;
	private String sTestCaseJavaFile = null;
	private String sTestCasePackageName = null;
	private String sTestCaseTestSuite = null;
	private String pathToFile = null;
	// Regex patterns for parsing test case data
	private Matcher matcherOuter = null;
	private Matcher matcherInner = null;
	private final Pattern testCaseAnnotation = Pattern.compile("@Test");
	private final Pattern testCaseName = Pattern.compile("\\s+(\\w+?)\\(\\)");
	private final Pattern testCaseJavaFile = Pattern.compile("\\s+(.+\\.java)\\s+");
	private final Pattern testCasePackageName = Pattern.compile("(.*?)\\s+\\[in\\s+.*\\s+\\[in\\s+(.*?)\\]\\]");
	private final Pattern testCaseTestSuite = Pattern.compile("\\[in\\s+.*\\s+\\[in\\s+(.*?)\\]\\]");

	/**
	 * Creates StringTestRunSession instance containing string data of
	 * ITestRunSession object.
	 * 
	 * @param session
	 */
	public StringTestRunSession(ITestRunSession session) {
		if (session != null)
			testRunSession = session.toString();
		else
			testRunSession = null;
		testRunName = session.getTestRunName();
		elapsedTime = session.getElapsedTimeInSeconds();
		failureTrace = session.getFailureTrace() != null ? session.getFailureTrace().getActual() : null;
		testResultNoChildren = new StringResult(session.getTestResult(false));
		testResultWithChildren = new StringResult(session.getTestResult(true));
		log.debug("PATH of project: " + session.getLaunchedProject().getProject().getFullPath().toOSString());
		log.debug("testRunSession:" + testRunSession);
		log.debug("testRunName:" + testRunName);
		log.debug("getElementName: " + session.getLaunchedProject().getElementName());
		log.debug("WORKSPACE PATH: " + System.getProperty("user.dir"));
		try {
			log.debug("PATH?: " + session.getLaunchedProject().getJavaProject().getOutputLocation());
		} catch (JavaModelException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		// log.debug(session.getLaunchedProject().getProject().getWorkingLocation());

		log.debug("getTestRunSession.getTestRunName: " + session.getTestRunSession().getTestRunName());

		// get test case information: name, java file, package and test suite
		try {
			for (IPackageFragmentRoot r : session.getLaunchedProject().getAllPackageFragmentRoots()) {
				for (IJavaElement e : r.getChildren()) {
					if (e.getResource() != null)
						if (e.getResource() != null) {
							if (e.getOpenable() != null) {
								String s = e.getOpenable().toString();
								matcherOuter = testCaseJavaFile.matcher(s);
								// For each Java file found
								while (matcherOuter.find()) {
									sTestCaseJavaFile = matcherOuter.group(1);
									// find test suite
									matcherInner = testCaseTestSuite.matcher(s);
									if (matcherInner.find())
										sTestCaseTestSuite = matcherInner.group(1);
									// find test package
									matcherInner = testCasePackageName.matcher(s);
									if (matcherInner.find())
										sTestCasePackageName = matcherInner.group(1);
									// find and add test cases to list
									// FIXME: path to workspace or project
									// location
									pathToFile = "/home/mcoufal/workspace" + e.getPath().toOSString() + "/"
											+ sTestCaseJavaFile;
									handleTestCasesinFile(pathToFile);
								}
								System.out.println("PATH toOSString: " + e.getPath().toOSString());
								System.out.println("getUnderlyingResource: " + e.getUnderlyingResource().getName());
							}
						}
				}
			}
		} catch (JavaModelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * Finds all test cases annotated with @Test in file in path given by
	 * argument. Sets sTestCaseName variable and adds new StringTestCase to
	 * testCases list.
	 *
	 * @param pathToFile
	 */
	private void handleTestCasesinFile(String pathToFile) {
		try (BufferedReader br = new BufferedReader(new FileReader(pathToFile))) {
			String currentLine;
			Boolean testAnnotationFound = false;

			// for each line in file
			while ((currentLine = br.readLine()) != null) {
				// look for test case annotation - @Test
				matcherInner = testCaseAnnotation.matcher(currentLine);
				if (matcherInner.find()) {
					testAnnotationFound = true;
				}
				if (testAnnotationFound) {
					// look for test case name
					matcherInner = testCaseName.matcher(currentLine);
					if (matcherInner.find()) {
						testAnnotationFound = false;
						sTestCaseName = matcherInner.group(1);
						log.debug(String.format("TestCase NAME: <%s>", sTestCaseName));
						log.debug(String.format("TestCase JAVA FILE: <%s>", sTestCaseJavaFile));
						log.debug(String.format("TestCase PACKAGE: <%s>", sTestCasePackageName));
						log.debug(String.format("TestCase TEST SUITE: <%s>", sTestCaseTestSuite));
						testCases.add(new StringTestCase(sTestCaseName, sTestCaseJavaFile, sTestCasePackageName,
								sTestCaseTestSuite));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * TODO not complete
	 */
	public void print() {
		System.out.println("[testRunSession]: " + testRunSession);
		System.out.println("[testRunName]: " + testRunName);
		System.out.println("[elapsedTime]: " + elapsedTime);
		System.out.println("[failureTrace]: " + failureTrace);
		testResultNoChildren.print();
		testResultWithChildren.print();
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
}
