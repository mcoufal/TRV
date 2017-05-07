package main.java.view;

import java.io.IOException;
import java.net.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.jboss.reddeer.common.logging.Logger;

import main.java.client.ResultsClient;
import mcoufal.inrunjunit.server.ResultsData;

/**
 * This is main class of TRView.
 * 
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 */
public class TRView {
	// set up logger
	private final static Logger log = Logger.getLogger(TRView.class);
	// IP address of machine where results server runs
	private static String serverIP = null;
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
	private static Shell shell;
	private static Shell connectShell;
	private static Label lblServerIP;
	private static Label lblPort;
	private static CLabel lblRuns;
	private static Button btnConnect;
	private static Button btnCancel;
	private static Label lblWarning;
	private static CLabel lblErrors;
	private static CLabel lblFailures;
	private static CLabel lblIgnored;
	private static Tree tree;
	// MENU
	private static Menu menuBar;
	private static Menu trviewMenu;
	private static Menu optionsMenu;
	private static MenuItem trviewMenuHeader;
	private static MenuItem optionsMenuHeader;
	private static MenuItem connectItem;
	private static MenuItem disconnectItem;
	private static MenuItem reconnectItem;
	private static MenuItem exitItem;
	private static MenuItem onTopItem;
	private static MenuItem resizableItem;
	// PREFERENCES
	private static final Point minimumShellSize = new Point(300, 212);
	private static final Point defaultShellSize = new Point(400, 312);

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

		// tree node selection - show stack trace
		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {

				TreeItem t = (TreeItem) event.item;
				// show stack trace if available
				for (ResultsData data : resClient.getDataSet()) {
					if (data.getTestCaseElement() == null)
						continue;
					// check if same item
					// Note: the substring dance - we need just method name, not
					// elapsed time
					int helpIndex = t.getText().lastIndexOf(' ');
					if (helpIndex >= 0) {
						if (t.getText().substring(0, helpIndex)
								.equals(data.getTestCaseElement().getTestCase().getName())) {
							String trace = data.getTestCaseElement().getFailureTrace();
							if (trace == null) {
								trace = "no trace";
								txtTrace.setEnabled(false);
							} else
								txtTrace.setEnabled(true);
							txtTrace.setText(trace);
						}
					}
					// empty trace
					else {
						txtTrace.setText("");
					}
				}
			}
		});

		// Menu -> TRView -> Connect...
		connectItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				createConnectShell();
				// open connect shell
				connectShell.open();
				connectShell.layout();
			}
		});

		// Menu -> TRView -> Re-connect
		reconnectItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// initialize results client end
				endResultsClient();
				// clear data
				clearGuiData();
				// create new client
				if (serverIP != null) {
					resClient = new ResultsClient(serverIP, portNum);
					resClient.start();
				}
			}
		});

		// Menu -> TRView -> Disconnect
		disconnectItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// initialize results client end
				endResultsClient();
				resClient = null;
			}
		});

		// Menu -> TRView -> Exit
		exitItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// initialize results client end
				endResultsClient();
				// end GUI
				shell.dispose();
			}
		});

		/*--- application runtime ---*/
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Redraws all needful components.
	 *
	 * Note: Needful are all components that may be changed while parsing
	 * results.
	 */
	protected static void redrawAllComponents() {
		if (!txtServerIP.isDisposed())
			txtServerIP.redraw();
		if (!txtPort.isDisposed())
			txtPort.redraw();
		if (!txtRuns.isDisposed())
			txtRuns.redraw();
		if (!txtErrors.isDisposed())
			txtErrors.redraw();
		if (!txtFailures.isDisposed())
			txtFailures.redraw();
		if (!txtIgnored.isDisposed())
			txtIgnored.redraw();
		if (!txtTrace.isDisposed())
			txtTrace.redraw();
		if (!shell.isDisposed())
			shell.redraw();
		if (!lblServerIP.isDisposed())
			lblServerIP.redraw();
		if (!lblPort.isDisposed())
			lblPort.redraw();
		if (!lblRuns.isDisposed())
			lblRuns.redraw();
		if (!btnConnect.isDisposed())
			btnConnect.redraw();
		if (!lblErrors.isDisposed())
			lblErrors.redraw();
		if (!lblFailures.isDisposed())
			lblFailures.redraw();
		if (!lblIgnored.isDisposed())
			lblIgnored.redraw();
		if (!tree.isDisposed())
			tree.redraw();
	}

	/**
	 * Initiates GUI.
	 */
	private static void createGUI() {
		log.info("Creating GUI");

		// shell and display
		display = Display.getDefault();
		Rectangle screenSize = display.getBounds();
		shell = new Shell(SWT.RESIZE | SWT.CLOSE | SWT.BORDER | SWT.ON_TOP);
		shell.setLayout(new GridLayout(8, false));
		shell.setMinimumSize(minimumShellSize);
		shell.setSize(defaultShellSize);
		shell.setLocation(screenSize.width - defaultShellSize.x, screenSize.height - defaultShellSize.y);

		// menu bar
		menuBar = new Menu(shell, SWT.BAR);
		trviewMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		trviewMenuHeader.setText("&TRView");
		optionsMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		optionsMenuHeader.setText("&Options");
		// TRView menu
		trviewMenu = new Menu(shell, SWT.DROP_DOWN);
		trviewMenuHeader.setMenu(trviewMenu);
		connectItem = new MenuItem(trviewMenu, SWT.PUSH);
		connectItem.setText("&Connect...");
		reconnectItem = new MenuItem(trviewMenu, SWT.PUSH);
		reconnectItem.setText("Re-connect");
		disconnectItem = new MenuItem(trviewMenu, SWT.PUSH);
		disconnectItem.setText("Disconnect");
		exitItem = new MenuItem(trviewMenu, SWT.PUSH);
		exitItem.setText("&Exit");
		// Options menu
		optionsMenu = new Menu(shell, SWT.DROP_DOWN);
		optionsMenuHeader.setMenu(optionsMenu);
		onTopItem = new MenuItem(optionsMenu, SWT.CHECK);
		onTopItem.setText("On-top");
		onTopItem.setSelection(true);
		resizableItem = new MenuItem(optionsMenu, SWT.CHECK);
		resizableItem.setText("Resizable");
		resizableItem.setSelection(true);
		// set shell's menu bar
		shell.setMenuBar(menuBar);

		// runs
		lblRuns = new CLabel(shell, SWT.RIGHT);
		lblRuns.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		lblRuns.setText("Runs:");
		lblRuns.setImage(new Image(display, TRView.class.getResourceAsStream("/test.png")));
		txtRuns = new Text(shell, SWT.BORDER | SWT.CENTER);
		txtRuns.setEnabled(false);
		txtRuns.setEditable(false);
		txtRuns.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtRuns.setText("");

		// failures
		lblFailures = new CLabel(shell, SWT.RIGHT);
		lblFailures.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		lblFailures.setText("Failures:");
		lblFailures.setImage(new Image(display, TRView.class.getResourceAsStream("/testfail.png")));
		txtFailures = new Text(shell, SWT.BORDER | SWT.CENTER);
		txtFailures.setEditable(false);
		txtFailures.setEnabled(false);
		txtFailures.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtFailures.setText("");

		// errors
		lblErrors = new CLabel(shell, SWT.RIGHT);
		lblErrors.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		lblErrors.setText("Errors:");
		lblErrors.setImage(new Image(display, TRView.class.getResourceAsStream("/testerr.png")));
		txtErrors = new Text(shell, SWT.BORDER | SWT.CENTER);
		txtErrors.setEnabled(false);
		txtErrors.setEditable(false);
		txtErrors.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtErrors.setText("");

		// ignored
		lblIgnored = new CLabel(shell, SWT.RIGHT);
		lblIgnored.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		lblIgnored.setText("Ignored:");
		lblIgnored.setImage(new Image(display, TRView.class.getResourceAsStream("/testignored.gif")));
		txtIgnored = new Text(shell, SWT.BORDER | SWT.CENTER);
		txtIgnored.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtIgnored.setEnabled(false);
		txtIgnored.setEditable(false);
		txtIgnored.setText("");

		// tree structure of test suites and test cases
		tree = new Tree(shell, SWT.SINGLE | SWT.BORDER);
		tree.setEnabled(false);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 8, 1));

		// trace
		CLabel lblTrace = new CLabel(shell, SWT.NONE);
		lblTrace.setText("Trace:");
		lblTrace.setImage(new Image(display, TRView.class.getResourceAsStream("/stkfrm_obj.png")));

		txtTrace = new StyledText(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		txtTrace.setEnabled(false);
		txtTrace.setEditable(false);
		txtTrace.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 8, 2));

		// open and layout shell
		shell.open();
		shell.layout();
	}

	private static void createConnectShell() {
		// create connect shell
		connectShell = new Shell(SWT.CLOSE | SWT.BORDER | SWT.ON_TOP);
		connectShell.setSize(defaultShellSize.x / 2, defaultShellSize.y / 2);
		Point childShellLocation = new Point(
				shell.getLocation().x + (shell.getSize().x / 2) - (connectShell.getSize().x / 2),
				shell.getLocation().y + (shell.getSize().y / 2) - (connectShell.getSize().y / 2));
		connectShell.setLocation(childShellLocation);
		connectShell.setLayout(new GridLayout(2, false));

		// warning label
		lblWarning = new Label(connectShell, SWT.CENTER);
		lblWarning.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));

		// IP address
		lblServerIP = new Label(connectShell, SWT.HORIZONTAL);
		lblServerIP.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		lblServerIP.setText("Server IP:");
		txtServerIP = new Text(connectShell, SWT.BORDER);
		txtServerIP.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		// TODO: Pre-set values - DEBUG PURPOSES - DELETE WHEN DONE
		txtServerIP.setText("127.0.0.1");

		// port number
		lblPort = new Label(connectShell, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		lblPort.setText("Port:");
		txtPort = new Text(connectShell, SWT.BORDER);
		txtPort.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		// TODO: Pre-set values - DEBUG PURPOSES - DELETE WHEN DONE
		txtPort.setText("7357");

		// cancel button
		btnCancel = new Button(connectShell, SWT.NONE);
		btnCancel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));
		btnCancel.setText("Cancel");

		// connect button
		btnConnect = new Button(connectShell, SWT.NONE);
		btnConnect.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		btnConnect.setText("Connect");

		// cancel button click listener
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// close connect shell
				connectShell.close();
			}
		});

		// connect button click listener
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// end results client
				endResultsClient();
				// clear old data
				clearGuiData();
				// get server information
				serverIP = txtServerIP.getText();
				if (serverIP != null){
					try{
						portNum = Integer.parseInt(txtPort.getText());
						// create new client
						resClient = new ResultsClient(serverIP, portNum);
						resClient.start();
						// close connect shell
						// connectShell.close();
					} catch (NumberFormatException nfe){
						lblWarning.setText("Port number is not valid!");
						lblWarning.setForeground(display.getSystemColor(SWT.COLOR_RED));
					}
				}
				else {
					lblWarning.setText("IP Adress is not valid!");
					lblWarning.setForeground(display.getSystemColor(SWT.COLOR_RED));
				}
			}
		});
	}

	/**
	 * Sets data of all components that are changed in ResultsParser to default
	 * values.
	 */
	private static void clearGuiData() {
		txtRuns.setText("");
		txtErrors.setText("");
		txtFailures.setText("");
		txtIgnored.setText("");
		txtTrace.setText("");
		for (TreeItem item : tree.getItems()) {
			disposeTreeItemsRecurse(item);
		}
		tree.clearAll(true);
		tree.setEnabled(true);
	}

	/**
	 * Disposes all items in tree structure given by node TreeItem.
	 * @param item
	 */
	private static void disposeTreeItemsRecurse(TreeItem item) {
		if (item == null) return;
		for (TreeItem child : item.getItems()) {
			disposeTreeItemsRecurse(child);
		}
		item.dispose();
	}

	/**
	 * Initiates Results client end by causing IOException.
	 *
	 * Note: Results client's thread is probably waiting on readObject() method
	 * and it is not interruptible. This method calls close on object input
	 * stream.
	 */
	private static void endResultsClient() {
		try {
			if (resClient != null) {
				resClient.endClient();
				if (resClient.getObjectInputStream() != null) {
					resClient.getObjectInputStream().close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	 * @return the Shell
	 */
	public static Shell getShell() {
		return shell;
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

	/**
	 * @return connect shell
	 */
	public static Shell getConnectShell() {
		return connectShell;
	}

	/**
	 * @return warning label
	 */
	public static Label getLblWarning() {
		return lblWarning;
	}
}
