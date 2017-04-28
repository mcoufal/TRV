package main.java.view;

import java.io.IOException;
import java.net.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
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

import main.java.client.ResultsClient;

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

	// variables for GUI
	private static Text txtServerIP;
	private static Text txtPort;
	private static Text txtRuns;
	private static Text txtErrors;
	private static Text txtFailures;
	private static Text txtIgnored;
	private static StyledText txtTrace;
	private static Display display;
	private static Shell shlErrors;
	private static Label lblServerIP;
	private static Label lblPort;
	private static CLabel lblRuns;
	private static Button btnConnect;
	private static CLabel lblErrors;
	private static CLabel lblFailures;
	private static CLabel lblIgnored;
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

		// tree node selection - show stack trace
		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {

				TreeItem t = (TreeItem) event.item;
				// show stack trace if available
				for (ResultsData data : resClient.getDataSet()) {
					if (data.getTestCaseElement() == null)
						continue;
					// check if same item
					if (t.getText().equals(data.getTestCaseElement().getTestMethodName())) {
						String trace = data.getTestCaseElement().getFailureTrace();
						if (trace == null){
							trace = "no trace";
							txtTrace.setEnabled(false);
						}
						else txtTrace.setEnabled(true);
						txtTrace.setText(trace);
					}
				}
			}
		});

		int scrollPos = 0;
		int lastScrollPos = scrollPos;
		// application runtime
		while (!shlErrors.isDisposed()) {
			//if (!display.readAndDispatch()) {
			//	display.sleep();
			//}
			display.readAndDispatch();
			scrollPos = getTree().getVerticalBar().getSelection();
			if (scrollPos != lastScrollPos){
				lastScrollPos = scrollPos;
				log.debug(String.format("vert bar selection: [%s]", TRView.getTree().getVerticalBar().getSelection()));
				log.debug(String.format("my gues: [%s]", TRView.getTree().getVerticalBar().getMaximum() - TRView.getTree().getVerticalBar().getThumb()));
				log.debug(String.format("vert bar maximum: [%s]", TRView.getTree().getVerticalBar().getMaximum()));
				log.debug(String.format("vert bar thumb: [%s]", TRView.getTree().getVerticalBar().getThumb()));
				log.debug(String.format("vert bar size[width, height]: [%s,%s]", TRView.getTree().getVerticalBar().getSize().x, TRView.getTree().getVerticalBar().getSize().y));
				log.debug(String.format("vert bar thumb bounds[x,y,width,height]: [%s,%s,%s,%s]", TRView.getTree().getVerticalBar().getThumbBounds().x, TRView.getTree().getVerticalBar().getThumbBounds().y, TRView.getTree().getVerticalBar().getThumbBounds().width, TRView.getTree().getVerticalBar().getThumbBounds().height));
			}
		}
		log.debug("Main application shell is disposed!");
	}

	/**
	 * TODO: is public (should be here some warning?)
	 * Redraws all components.
	 */
	public static void redrawAllComponents() {
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
		shlErrors = new Shell(SWT.RESIZE | SWT.CLOSE | SWT.ON_TOP);
		shlErrors.setSize(450, 296);
		shlErrors.setLayout(new GridLayout(8, false));
		Rectangle r = display.getBounds();
		shlErrors.setLocation(r.width - 450, r.height - 296);

		// IP address
		lblServerIP = new Label(shlErrors, SWT.HORIZONTAL);
		lblServerIP.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
		lblServerIP.setText("Server IP:");
		txtServerIP = new Text(shlErrors, SWT.BORDER);
		txtServerIP.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));

		// port number
		lblPort = new Label(shlErrors, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
		lblPort.setText("Port:");
		txtPort = new Text(shlErrors, SWT.BORDER);
		txtPort.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));

		// connect button
		btnConnect = new Button(shlErrors, SWT.NONE);
		btnConnect.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		btnConnect.setText("Connect");

		// runs
		lblRuns = new CLabel(shlErrors, SWT.NONE);
		lblRuns.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblRuns.setText("Runs:");
		lblRuns.setImage(new Image(display, "icons/test.png"));
		txtRuns = new Text(shlErrors, SWT.BORDER);
		txtRuns.setEnabled(false);
		txtRuns.setEditable(false);
		txtRuns.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txtRuns.setText("0/0");

		// errors
		lblErrors = new CLabel(shlErrors, SWT.NONE);
		lblErrors.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblErrors.setText("Errors:");
		lblErrors.setImage(new Image(display, "icons/testerr.png"));
		txtErrors = new Text(shlErrors, SWT.BORDER);
		txtErrors.setEnabled(false);
		txtErrors.setEditable(false);
		txtErrors.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txtErrors.setText("0");

		// failures
		lblFailures = new CLabel(shlErrors, SWT.NONE);
		lblFailures.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblFailures.setText("Failures:");
		lblFailures.setImage(new Image(display, "icons/testfail.png"));
		txtFailures = new Text(shlErrors, SWT.BORDER);
		txtFailures.setEditable(false);
		txtFailures.setEnabled(false);
		txtFailures.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txtFailures.setText("0");

		// ignored
		lblIgnored = new CLabel(shlErrors, SWT.NONE);
		lblIgnored.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblIgnored.setText("Ignored:");
		lblIgnored.setImage(new Image(display, "icons/testignored.gif"));
		txtIgnored = new Text(shlErrors, SWT.BORDER);
		txtIgnored.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txtIgnored.setEnabled(false);
		txtIgnored.setEditable(false);
		txtIgnored.setText("0");

		// tree structure of test suites and test cases
		tree = new Tree(shlErrors, SWT.BORDER);
		tree.setEnabled(false);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 8, 1));

		// trace
		CLabel lblTrace = new CLabel(shlErrors, SWT.NONE);
		lblTrace.setText("Trace:");
		lblTrace.setImage(new Image(display, "icons/stkfrm_obj.png"));

		txtTrace = new StyledText(shlErrors, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL | SWT.CANCEL | SWT.MULTI);
		txtTrace.setEnabled(false);
		txtTrace.setEditable(false);
		txtTrace.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 8, 1));

		shlErrors.open();
		shlErrors.layout();
	}

	/**
	 * @return the lblRuns
	 */
	public static CLabel getLblRuns() {
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
	 * @return the txtRuns
	 */
	public static Text getTxtRuns() {
		return txtRuns;
	}

	/**
	 * @return the txtErrors
	 */
	public static Text getTxtErrors() {
		return txtErrors;
	}

	/**
	 * @return the txtFailures
	 */
	public static Text getTxtFailures() {
		return txtFailures;
	}

	/**
	 * @return the txtIgnored
	 */
	public static Text getTxtIgnored() {
		return txtIgnored;
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
	public static CLabel getLblErrors() {
		return lblErrors;
	}

	/**
	 * @return the lblFailures
	 */
	public static CLabel getLblFailures() {
		return lblFailures;
	}
}
