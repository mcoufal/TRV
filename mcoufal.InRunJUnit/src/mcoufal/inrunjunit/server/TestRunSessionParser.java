package mcoufal.inrunjunit.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.junit.model.ITestRunSession;

/**
 * TODO
 *
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 */
public class TestRunSessionParser {
	private List<StringTestCase> testCases = new ArrayList<StringTestCase>();
	private String sTestCaseName = null;
	private String sTestCaseJavaFile = null;
	private String sTestCasePackageName = null;
	private String sTestCaseTestSuite = null;
	private String pathToFile = null;
	private String pathToWorkspace = System.getenv("TESTS_DIR");
	// Regex patterns for parsing test case data
	private Matcher matcherOuter = null;
	private Matcher matcherInner = null;
	private final Pattern testCaseAnnotation = Pattern.compile("@Test");
	private final Pattern testCaseName = Pattern.compile("\\s+(\\w+?)\\(\\)");
	private final Pattern testCaseJavaFile = Pattern.compile("(?m)^\\s+(.+)\\.java$");
	private final Pattern testCasePackageName = Pattern.compile("(.*?)\\s+\\[in\\s+.*\\s+\\[in\\s+(.*?)\\]\\]");

	public TestRunSessionParser(ITestRunSession session) {
		// if path to tests is not set explicitly, use workspace
		if (pathToWorkspace == null)
			pathToWorkspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
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
									int helpIndex = session.getTestRunName().indexOf(' ');
									if (helpIndex > 0)
										sTestCaseTestSuite = session.getTestRunName().substring(0, helpIndex);
									else
										sTestCaseTestSuite = session.getTestRunName();
									// find test package
									matcherInner = testCasePackageName.matcher(s);
									if (matcherInner.find())
										sTestCasePackageName = matcherInner.group(1);
									// find and add test cases to list
									pathToFile = pathToWorkspace + e.getPath().toOSString() + "/"
											+ sTestCaseJavaFile + ".java";
									handleTestCasesinFile(pathToFile);
								}
							}
						}
				}
			}
		} catch (JavaModelException e1) {
			// TODO
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
	 * @return the testCases
	 */
	public List<StringTestCase> getTestCases() {
		return testCases;
	}
}
