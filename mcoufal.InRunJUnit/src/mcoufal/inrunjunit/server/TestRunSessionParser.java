package mcoufal.inrunjunit.server;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
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

	public TestRunSessionParser(ITestRunSession session, Boolean closeResources) {
		// get test case information: name, java file, package and test suite
		try {
			for (IPackageFragmentRoot r : session.getLaunchedProject().getAllPackageFragmentRoots()) {
				for (IJavaElement j : r.getChildren()) {
					if (j.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
						IPackageFragment frag = j.getAdapter(IPackageFragment.class);
						for (ICompilationUnit unit : frag.getCompilationUnits()) {
							if (unit.isOpen()) {
								for (IType t : unit.getAllTypes()) {
									IMethod[] methods = t.getMethods();
									if (methods != null) {
										for (IMethod m : t.getMethods()) {
											if (m.getAnnotation("Test").exists()) {
												sTestCaseName = m.getElementName();
												sTestCaseJavaFile = unit.getElementName().substring(0,
														unit.getElementName().indexOf('.'));
												sTestCasePackageName = frag.getElementName();
												// find test suite (test run) name
												int helpIndex = session.getTestRunName().indexOf(' ');
												if (helpIndex > 0)
													sTestCaseTestSuite = session.getTestRunName().substring(0, helpIndex);
												else
													sTestCaseTestSuite = session.getTestRunName();
												testCases.add(new StringTestCase(sTestCaseName, sTestCaseJavaFile,
														sTestCasePackageName, sTestCaseTestSuite));
											}
										}
									}
								}
								if (closeResources)
									unit.close();
							}
						}
					}
				}

			}
		} catch (JavaModelException e1) {
			System.err.println("Failed in parsing test run session! See stack trace for details.");
			e1.printStackTrace();
		}
	}

	/**
	 * @return the testCases
	 */
	public List<StringTestCase> getTestCases() {
		return testCases;
	}
}
