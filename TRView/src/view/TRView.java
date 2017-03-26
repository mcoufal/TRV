package view;

import java.io.IOException;
import java.net.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
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
	private static Text text;
	private static Text text_1;
	private static Text text_2;
	private static Text text_3;
	private static Display display;
	private static Shell shlErrors;
	private static Label lblServerIP;
	private static Label lblPort;
	private static Label lblRuns;
	
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
		//app = new TRView();
		// set up variables
		oldData = null;
		initialDataParsed = false;

		// create GUI
		createGUI();

		// Event listeners
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
					ResultsParser.parseAndDisplay(resClient.getInitialDataSet());
					// TODO: method redraw all components
					TRView.getTree().redraw();
					TRView.getLblRuns().redraw();
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
	 * TODO: maybe will need to reference all variables as app.[get]<variable>
	 */
	private static void createGUI() {
		log.info("Creating GUI");
		// shell and display
		display = Display.getDefault();
		shlErrors = new Shell();
		shlErrors.setSize(450, 296);
		shlErrors.setLayout(new GridLayout(6, false));

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
		text = new Text(shlErrors, SWT.BORDER);
		text.setEnabled(false);
		text.setEditable(false);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblErrors = new Label(shlErrors, SWT.NONE);
		lblErrors.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblErrors.setText("Errors:");
		text_1 = new Text(shlErrors, SWT.BORDER);
		text_1.setEnabled(false);
		text_1.setEditable(false);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblFailures = new Label(shlErrors, SWT.NONE);
		lblFailures.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFailures.setText("Failures:");
		text_2 = new Text(shlErrors, SWT.BORDER);
		text_2.setEditable(false);
		text_2.setEnabled(false);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		// tree structere of test suites and test cases
		tree = new Tree(shlErrors, SWT.BORDER);
		tree.setEnabled(false);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1));

		// trace
		Label lblTrace = new Label(shlErrors, SWT.NONE);
		lblTrace.setText("Trace:");

		text_3 = new Text(shlErrors, SWT.BORDER);
		text_3.setEnabled(false);
		text_3.setEditable(false);
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 6, 3));

		shlErrors.open();
		shlErrors.layout();
	}
}
