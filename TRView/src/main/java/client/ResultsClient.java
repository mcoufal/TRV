package main.java.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

import org.jboss.reddeer.common.logging.Logger;

import com.mcoufal.inrunjunit.listener.JUnitListenerEP.Phase;
import com.mcoufal.inrunjunit.server.ResultsData;

import main.java.view.ResultsParser;
import main.java.view.TRView;

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
	public static ObjectInputStream fromServer = null;
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

		// create socket connection
		try {
			sock = new Socket(IPaddr, portNum);
		} catch (IOException e) {
			log.error("Error while connecting to '" + IPaddr + "' at port " + portNum);
			e.printStackTrace();
		}

		// establish input object stream
		try {
			fromServer = new ObjectInputStream(sock.getInputStream());
		} catch (IOException e) {
			log.error("Error while creating input object stream");
			e.printStackTrace();
		}

		// receive initial results data set
		try {
			resultsList = (List<ResultsData>) fromServer.readObject();
		} catch (ClassNotFoundException | IOException e) {
			log.error("Failed to read data from server!");
			e.printStackTrace();
		}

		// parse and display
		TRView.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				ResultsParser.parseAndDisplay(resultsList);
			}
		});

		// Communication with server - receiving and sending data
		while (alive) {
			try {
				receivedData = (ResultsData) fromServer.readObject();
			} catch (ClassNotFoundException cnfe) {
				log.error("Error while reading data from the server: " + cnfe.getMessage());
				cnfe.printStackTrace();
			} catch (IOException ioe) {
				// end client
				break;
			}

			// add received data to list
			resultsList.add(receivedData);

			// parse and display
			TRView.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					ResultsParser.parseAndDisplay(receivedData);
				}
			});

			// end results client if session finished
			if (receivedData.getPhase().equals(Phase.SESSION_FINISHED))
				alive = false;
		}

		// close resources
		try {
			fromServer.close();
		} catch (IOException e) {
			// empty
		}
		try {
			sock.close();
		} catch (IOException e) {
			// empty
		}
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
	 * Returns input data stream used to get data from server.
	 *
	 * @return input data stream ObjectInputStream.
	 */
	public ObjectInputStream getObjectInputStream() {
		return fromServer;
	}

	/**
	 * Initiates ResultsClient end.
	 *
	 * Note: ResultsClient instance may be jammed on readObject() method. In
	 * this case calling this method won't have any effect until client receives
	 * any data. For immediate ending close clients object input stream after
	 * calling this method.
	 */
	public void endClient() {
		alive = false;
	}
}
