package view;

import java.io.IOException;
import java.net.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.jboss.reddeer.common.logging.Logger;

import com.mcoufal.inrunjunit.server.ResultsData;

import client.ResultsClient;

/**
 * TODO: comments
 * 
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 */
public class TRView {
	// set up logger
	private final static Logger log = Logger.getLogger(TRView.class);
	// IP address of machine where results server runs
	private static String serverIP;
	// port of results server service
	private static int portNum;
	// client for receiving results from results server
	private static ResultsClient resClient = null;
	// TODO: There may be some better way to do it, but for now:
	// variable used to determine if data is changed
	private static ResultsData oldData;
	// TODO;
	private static Boolean initialDataParsed;

	// variables for GUI
	private static Text txtServerIP;
	private static Text txtPort;
	private static Text txtRuns;
	private static Text txtErrors;
	private static Text txtFailures;
	private static StyledText txtTrace;
	private static Display display;
	private static Shell shlErrors;
	private static Label lblServerIP;
	private static Label lblPort;
	private static Label lblRuns;
	private static Button btnConnect;
	private static Label lblErrors;
	private static Label lblFailures;
	private static Tree tree;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		log.info("TRView app started");
		// save reference to this app to get access to variables
		// app = new TRView();
		// set up variables
		oldData = null;
		initialDataParsed = false;

		// create GUI
		createGUI();

		/*--- Event listeners ---*/

		// connect button
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				log.debug("Connect button mouse down");
				// TODO : osetrit vstupy, destroy old clients?
				serverIP = txtServerIP.getText();
				portNum = Integer.parseInt(txtPort.getText());
				resClient = new ResultsClient(serverIP, portNum);
				resClient.start();
				tree.setEnabled(true);
			}
		});

		// tree node selection
		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {

				// highlight clicked item and cancel other highlighting
				TreeItem t = (TreeItem) event.item;
				for (TreeItem currentNode : tree.getItems()) {
					if (t.getText().equals(currentNode.getText())) {
						t.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_SELECTION));
					} else {
						t.setBackground(null);
					}
				}

				// show stack trace if available
				for (ResultsData data : resClient.getDataSet()) {
					if (data.getTestCaseElement() == null)
						continue;
					// check if same item
					if (t.getText().equals(data.getTestCaseElement().getTestMethodName())) {
						String trace = data.getTestCaseElement().getFailureTrace();
						if (trace == null)
							trace = "TEST: Nothing in trace.\nHey multiple\nlines.";
						txtTrace.setText(trace);
					}
				}
			}
		});

		// application runtime
		while (!shlErrors.isDisposed()) {
			display.readAndDispatch(); // TODO: how to handle return value
										// properly?
			// if (!display.readAndDispatch()) {
			// display.sleep();
			// }

			// start to parse and display results
			if (resClient != null && resClient.initialDataSetRecieved()) {
				// parse initial data
				if (initialDataParsed == false) {
					ResultsParser.parseAndDisplay(resClient.getDataSet());
					// redraw all components
					redrawAllComponents();
					initialDataParsed = true;
				}
				// parse new data
				ResultsData newData = resClient.getData();
				// no data, no processing
				if (newData != null) {
					// process new data only
					// TODO: Ideally, try some thread sync trick like
					// Display.syncExec() from ResultsClient thread to parse
					// results! Or find another way how to handle this - at
					// least some FIFO system - we are losing data!
					if (!newData.equals(oldData)) {
						ResultsParser.parseAndDisplay(newData);
						// TODO: method redraw all components
						TRView.getTree().redraw();
						TRView.getLblRuns().redraw();
						oldData = newData;
					}
				}
			}
		}
		log.debug("Main application shell is disposed!");
	}

	/**
	 * Redraws all components.
	 */
	private static void redrawAllComponents() {
		txtServerIP.redraw();
		txtPort.redraw();
		txtRuns.redraw();
		txtErrors.redraw();
		txtFailures.redraw();
		txtTrace.redraw();
		shlErrors.redraw();
		lblServerIP.redraw();
		lblPort.redraw();
		lblRuns.redraw();
		btnConnect.redraw();
		lblErrors.redraw();
		lblFailures.redraw();
		tree.redraw();
	}

	/**
	 * Initiates GUI.
	 */
	private static void createGUI() {
		log.info("Creating GUI");
		// shell and display
		display = Display.getDefault();
		shlErrors = new Shell(SWT.BORDER | SWT.CLOSE | SWT.ON_TOP | SWT.RESIZE);
		shlErrors.setSize(450, 296);
		shlErrors.setLayout(new GridLayout(6, false));
		Rectangle r = display.getBounds();
		shlErrors.setLocation(r.width - 450, r.height - 296);

		// server options: IP address, port number, connect button
		lblServerIP = new Label(shlErrors, SWT.HORIZONTAL);
		lblServerIP.setAlignment(SWT.CENTER);
		lblServerIP.setText("Server IP:");
		txtServerIP = new Text(shlErrors, SWT.BORDER);

		lblPort = new Label(shlErrors, SWT.NONE);
		lblPort.setText("Port:");
		txtPort = new Text(shlErrors, SWT.BORDER);

		btnConnect = new Button(shlErrors, SWT.NONE);
		btnConnect.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		btnConnect.setText("Connect");

		// runs, errors, failures
		lblRuns = new Label(shlErrors, SWT.NONE);
		lblRuns.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRuns.setText("Runs:");
		txtRuns = new Text(shlErrors, SWT.BORDER);
		txtRuns.setEnabled(false);
		txtRuns.setEditable(false);
		txtRuns.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtRuns.setText("0/0");

		lblErrors = new Label(shlErrors, SWT.NONE);
		lblErrors.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblErrors.setText("Errors:");
		txtErrors = new Text(shlErrors, SWT.BORDER);
		txtErrors.setEnabled(false);
		txtErrors.setEditable(false);
		txtErrors.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtErrors.setText("0");

		lblFailures = new Label(shlErrors, SWT.NONE);
		lblFailures.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFailures.setText("Failures:");
		txtFailures = new Text(shlErrors, SWT.BORDER);
		txtFailures.setEditable(false);
		txtFailures.setEnabled(false);
		txtFailures.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtFailures.setText("0");

		// tree structure of test suites and test cases
		tree = new Tree(shlErrors, SWT.BORDER);
		tree.setEnabled(false);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1));

		// trace
		Label lblTrace = new Label(shlErrors, SWT.NONE);
		lblTrace.setText("Trace:");

		txtTrace = new StyledText(shlErrors, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL | SWT.CANCEL | SWT.MULTI);
		txtTrace.setEnabled(false);
		txtTrace.setEditable(false);
		txtTrace.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1));

		shlErrors.open();
		shlErrors.layout();
	}

	/**
	 * @return the lblRuns
	 */
	public static Label getLblRuns() {
		return lblRuns;
	}

	/**
	 * @return the lblRuns
	 */
	public static Tree getTree() {
		return tree;
	}

	/**
	 * @return the txtServerIP
	 */
	public static Text getTxtServerIP() {
		return txtServerIP;
	}

	/**
	 * @return the txtPort
	 */
	public static Text getTxtPort() {
		return txtPort;
	}

	/**
	 * @return the text
	 */
	public static Text getTxtRuns() {
		return txtRuns;
	}

	/**
	 * @return the txtLblErrors
	 */
	public static Text getTxtErrors() {
		return txtErrors;
	}

	/**
	 * @return the txtLblFailures
	 */
	public static Text getTxtLblFailures() {
		return txtFailures;
	}

	/**
	 * @return the txtTrace
	 */
	public static StyledText getTxtTrace() {
		return txtTrace;
	}

	/**
	 * @return the display
	 */
	public static Display getDisplay() {
		return display;
	}

	/**
	 * @return the shlErrors
	 */
	public static Shell getShlErrors() {
		return shlErrors;
	}

	/**
	 * @return the lblServerIP
	 */
	public static Label getLblServerIP() {
		return lblServerIP;
	}

	/**
	 * @return the lblPort
	 */
	public static Label getLblPort() {
		return lblPort;
	}

	/**
	 * @return the btnConnect
	 */
	public static Button getBtnConnect() {
		return btnConnect;
	}

	/**
	 * @return the lblErrors
	 */
	public static Label getLblErrors() {
		return lblErrors;
	}

	/**
	 * @return the lblFailures
	 */
	public static Label getLblFailures() {
		return lblFailures;
	}

	/**
	 * @param lblRuns
	 *            the lblRuns to set
	 */
	public static void setLblRuns(Label lblRuns) {
		TRView.lblRuns = lblRuns;
	}
}
