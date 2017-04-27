package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

import org.jboss.reddeer.common.logging.Logger;
import com.mcoufal.inrunjunit.server.ResultsData;

import view.ResultsParser;
import view.TRView;
// TODO : configuration file
// TODO : thread sync
/**
 * ResultsClient class connects to and communicates with ResultsServer. First,
 * ResultsClient establishes connection with ResultsServer and after that it is
 * handled by ClientHandler. Most recent data received from ClientHandler are
 * stored in 'receivedData' variable that is publicly available to any other
 * class.
 * 
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 */
public class ResultsClient extends Thread {
	// set up logger
	private final static Logger log = Logger.getLogger(ResultsClient.class);
	public List<ResultsData> resultsList = null;
	public static ResultsData receivedData = null;
	public static ObjectInputStream fromServer;
	private static String IPaddr;
	private static int portNum;
	private static Boolean alive;

	/**
	 * Constructor. Initializes IP address and port where is ResultsServer
	 * running.
	 * 
	 * @param serverIP
	 * @param portNum
	 */
	public ResultsClient(String serverIP, int portNum) {
		IPaddr = serverIP;
		ResultsClient.portNum = portNum;
		alive = true;
		log.info("ResultsClient created");
	}

	/**
	 * Thread run of client - getting results from results server.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		log.info("ResultsClient started: connecting to '" + IPaddr + "' at port " + portNum);
		Socket sock = null;
		fromServer = null;

		// create socket connection
		try {
			sock = new Socket(IPaddr, portNum);
		} catch (IOException e) {
			log.error("Error while connecting to '" + IPaddr + "' at port " + portNum);
			e.printStackTrace();
		}

		// establish object stream
		try {
			fromServer = new ObjectInputStream(sock.getInputStream());
		} catch (IOException e) {
			log.error("Error while creating input object stream");
			e.printStackTrace();
		}

		// receive initial results data set
		try {
			resultsList = (List<ResultsData>) fromServer.readObject();
			log.debug("Received initial data set: " + resultsList.toString());
			// parse and display
			TRView.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					ResultsParser.parseAndDisplay(resultsList);
					TRView.redrawAllComponents();
				}
			});
		} catch (ClassNotFoundException | IOException e) {
			log.error("Failed to read data from server!");
			e.printStackTrace();
		}

		// TODO: communication
		while (alive) {
			try { // receive results data
				receivedData = (ResultsData) fromServer.readObject();
				resultsList.add(receivedData);
				// parse and display
				TRView.getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						ResultsParser.parseAndDisplay(receivedData);
						TRView.redrawAllComponents();
					}
				});

				log.debug("Received: " + receivedData.toString() + " : " + receivedData.getPhase());
			} catch (ClassNotFoundException | IOException e) {
				log.error("Failed to read data from server!");
				e.printStackTrace();
			}
		}

		// close resources
		try {
			fromServer.close();
			sock.close();
		} catch (IOException e) {
			log.error("Failed to close client's sockets!");
			e.printStackTrace();
		}
	}

	/**
	 * TODO: ending client
	 */
	public void end() {
		log.info("Ending ResultsClient");
		alive = false;
	}

	/**
	 * Gets most recent ResultsData received from ResultsServer via it's
	 * ClientHandler.
	 * 
	 * @return test's ResultsData or null if no data were received so far.
	 */
	public ResultsData getData() {
		return receivedData;
	}

	/**
	 * Gets list of all ResultsData received from ResultsServer via it's
	 * ClientHandler. List of ResultsData contains all data collected from
	 * JUnitListenerEP before client connected to ResultsServer.
	 * 
	 * @return list of test's ResultsData or empty list if no data were
	 *         processed before connecting.
	 */
	public List<ResultsData> getDataSet() {
		return resultsList;
	}

	/**
	 * @return true if initial data set was received, false otherwise
	 */
	public Boolean initialDataSetRecieved() {
		return resultsList == null ? false : true;
	}
}
