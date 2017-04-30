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

import com.mcoufal.inrunjunit.server.ResultsData;

import main.java.client.ResultsClient;

/**
 * TODO: comments, all components in redraw method?, all (suitable) components
 * have their getters?
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
	private static Shell shell;
	private static Shell connectShell;
	private static Label lblServerIP;
	private static Label lblPort;
	private static CLabel lblRuns;
	private static Button btnConnect;
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
					if (t.getText().substring(0, t.getText().lastIndexOf(' '))
							.equals(data.getTestCaseElement().getTestMethodName())) {
						String trace = data.getTestCaseElement().getFailureTrace();
						if (trace == null) {
							trace = "no trace";
							txtTrace.setEnabled(false);
						} else
							txtTrace.setEnabled(true);
						txtTrace.setText(trace);
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
				// TODO: Unimplemented listener!
			}
		});

		// Menu -> TRView -> Disconnect
		disconnectItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// TODO: Unimplemented listener!
			}
		});

		// Menu -> TRView -> Exit
		exitItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// initialize results client end
				try {
					if (resClient != null)
						resClient.getObjectInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// end GUI
				shell.dispose();
			}
		});

		/*--- application runtime ---*/
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
			log.debug(String.format("Shell size[width,height]=[%s,%s]", shell.getSize().x, shell.getSize().y));
		}
	}

	/**
	 * TODO: is public (should be here some warning?) Redraws all components.
	 */
	public static void redrawAllComponents() {
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
		Point minimumShellSize = new Point(417,212);
		Point defaultShellSize = new Point(474, 312);

		// shell and display
		display = Display.getDefault();
		Rectangle screenSize = display.getBounds();
		shell = new Shell(SWT.RESIZE | SWT.CLOSE | SWT.BORDER | SWT.ON_TOP);
		shell.setLayout(new GridLayout(8, false));
		shell.setMinimumSize(minimumShellSize);
		shell.setSize(defaultShellSize);
		shell.setLocation(screenSize.width - defaultShellSize.x, screenSize.height - defaultShellSize.y);

		// TODO: menu bar
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
		lblRuns = new CLabel(shell, SWT.NONE);
		lblRuns.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblRuns.setText("Runs:");
		lblRuns.setImage(new Image(display, "icons/test.png"));
		txtRuns = new Text(shell, SWT.BORDER);
		txtRuns.setEnabled(false);
		txtRuns.setEditable(false);
		txtRuns.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		txtRuns.setText("0/0");

		// errors
		lblErrors = new CLabel(shell, SWT.NONE);
		lblErrors.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblErrors.setText("Errors:");
		lblErrors.setImage(new Image(display, "icons/testerr.png"));
		txtErrors = new Text(shell, SWT.BORDER);
		txtErrors.setEnabled(false);
		txtErrors.setEditable(false);
		txtErrors.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		txtErrors.setText("0");

		// failures
		lblFailures = new CLabel(shell, SWT.NONE);
		lblFailures.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblFailures.setText("Failures:");
		lblFailures.setImage(new Image(display, "icons/testfail.png"));
		txtFailures = new Text(shell, SWT.BORDER);
		txtFailures.setEditable(false);
		txtFailures.setEnabled(false);
		txtFailures.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		txtFailures.setText("0");

		// ignored
		lblIgnored = new CLabel(shell, SWT.NONE);
		lblIgnored.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblIgnored.setText("Ignored:");
		lblIgnored.setImage(new Image(display, "icons/testignored.gif"));
		txtIgnored = new Text(shell, SWT.BORDER);
		txtIgnored.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		txtIgnored.setEnabled(false);
		txtIgnored.setEditable(false);
		txtIgnored.setText("0");

		// tree structure of test suites and test cases
		tree = new Tree(shell, SWT.BORDER);
		tree.setEnabled(false);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 8, 1));

		// trace
		CLabel lblTrace = new CLabel(shell, SWT.NONE);
		lblTrace.setText("Trace:");
		lblTrace.setImage(new Image(display, "icons/stkfrm_obj.png"));

		txtTrace = new StyledText(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		txtTrace.setEnabled(false);
		txtTrace.setEditable(false);
		txtTrace.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 8, 1));

		// open and layout shell
		shell.open();
		shell.layout();
	}

	private static void createConnectShell() {
		// create connect shell
		connectShell = new Shell(SWT.CLOSE | SWT.ON_TOP);
		connectShell.setSize(shell.getSize().x / 2, shell.getSize().y / 2);
		Point childShellLocation = new Point(
				shell.getLocation().x + (shell.getSize().x / 2) - (connectShell.getSize().x / 2),
				shell.getLocation().y + (shell.getSize().y / 2) - (connectShell.getSize().y / 2));
		connectShell.setLocation(childShellLocation);
		connectShell.setLayout(new GridLayout(2, false));
		// IP address
		lblServerIP = new Label(connectShell, SWT.HORIZONTAL);
		lblServerIP.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblServerIP.setText("Server IP:");
		txtServerIP = new Text(connectShell, SWT.BORDER);
		txtServerIP.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		// port number
		lblPort = new Label(connectShell, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblPort.setText("Port:");
		txtPort = new Text(connectShell, SWT.BORDER);
		txtPort.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		// connect button
		btnConnect = new Button(connectShell, SWT.NONE);
		btnConnect.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 2, 1));
		btnConnect.setText("Connect");

		// connect button click listener
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				log.debug("Connect button mouse down");
				// TODO : osetrit vstupy, destroy old clients?
				// clear old data
				tree.clearAll(true);
				txtTrace.setText("");
				tree.setEnabled(true);
				// get server information
				serverIP = txtServerIP.getText();
				portNum = Integer.parseInt(txtPort.getText());
				// create new client
				resClient = new ResultsClient(serverIP, portNum);
				resClient.start();
				// close connect shell
				connectShell.close();
			}
		});
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
}
