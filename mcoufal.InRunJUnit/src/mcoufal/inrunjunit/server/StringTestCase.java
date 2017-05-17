package mcoufal.inrunjunit.server;

import java.io.Serializable;

/**
 * This class is used to store basic test case information.
 *
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 */
@SuppressWarnings("serial")
public class StringTestCase implements Serializable {
	private String name;
	private String javaFile;
	private String packageName;
	private String testSuite;

	/**
	 * Constructor.
	 *
	 * @param name
	 * @param javaFile
	 * @param packageName
	 * @param testSuite
	 */
	public StringTestCase(String name, String javaFile, String packageName, String testSuite) {
		this.name = name;
		this.javaFile = javaFile;
		this.packageName = packageName;
		this.testSuite = testSuite;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the javaFile
	 */
	public String getJavaFile() {
		return javaFile;
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @return the testSuite
	 */
	public String getTestSuite() {
		return testSuite;
	}

	/**
	 * Prints StringTestCase values to standard output.
	 */
	public void print() {
		System.out.println(String.format("[Test case name]: %s", name));
		System.out.println(String.format("[Test case java file]: %s", javaFile));
		System.out.println(String.format("[Test case package name]: %s", packageName));
		System.out.println(String.format("[Test case test suite name]: %s", testSuite));
	}
}
