package mcoufal.inrunjunit.server;

import java.io.Serializable;

/**
 * String representation of org.eclipse.jdt.junit.model.ITestElement.Result.
 *
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 */
@SuppressWarnings("serial")
public class StringResult implements Serializable {

	/**
	 * @return string representation of Result
	 */
	public String getResult() {
		return status;
	}

	private String status = null;

	/**
	 * @return string representation of Result
	 */
	@Override
	public String toString() {
		return status;
	}

	/**
	 * Constructor.
	 *
	 * Converts result to string representation.
	 *
	 * @param result
	 */
	public StringResult(org.eclipse.jdt.junit.model.ITestElement.Result result) {
		if (result != null) {
			if (result == org.eclipse.jdt.junit.model.ITestElement.Result.ERROR) {
				status = "Error";
			} else if (result == org.eclipse.jdt.junit.model.ITestElement.Result.FAILURE) {
				status = "Failure";
			} else if (result == org.eclipse.jdt.junit.model.ITestElement.Result.IGNORED) {
				status = "Ignored";
			} else if (result == org.eclipse.jdt.junit.model.ITestElement.Result.OK) {
				status = "OK";
			} else if (result == org.eclipse.jdt.junit.model.ITestElement.Result.UNDEFINED) {
				status = "Undefined";
			}
		} else
			status = "Undefined";
	}

	/**
	 * Prints result state to standard output.
	 */
	public void print() {
		System.out.println(status);
	}

}
