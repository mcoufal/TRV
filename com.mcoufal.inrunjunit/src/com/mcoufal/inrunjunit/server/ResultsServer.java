package com.mcoufal.inrunjunit.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.jboss.reddeer.common.logging.Logger;
import com.mcoufal.inrunjunit.listener.JUnitListenerEP.Phase;

/**
 * Represents server gathering test data from JUnitListener and sending this
 * data to all clients that had connected to this server. Manages list of
 * ClientHandlers to provide service for all clients.
 * 
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 */
public class ResultsServer extends Thread {
	// set up logger
	private final static Logger log = Logger.getLogger(ResultsServer.class);
	// TODO: set up one port number? Which one- ask guys @RHJB
	public static int portNum = 1234;
	// list of client connections
	private static List<ClientHandler> clientThreads = new ArrayList<ClientHandler>();
	// list of all ResultsData from JUnitListenerEP
	private static List<ResultsData> resultsList = new ArrayList<ResultsData>();

	/**
	 * Waiting for incoming client communication, creating new ClientHandlers
	 * for every client and managing those handlers.
	 */
	@Override
	public void run() {
		log.info("Server thread is running...");
		ServerSocket servSock = null;

		try {
			// establish server socket
			servSock = new ServerSocket(portNum);

			// looking for clients
			while (true) {
				log.info("Waiting for new client...");
				ClientHandler newClient = new ClientHandler(servSock.accept(), resultsList);
				clientThreads.add(newClient);
				newClient.start();
			}
		} catch (IOException e) {
			log.error("Failed to create server socket or connect with client!");
			e.printStackTrace();
			// TODO: destroy client list?
		}

	}

	/**
	 * Sends ITestCaseElement data to all clients who are connected to this
	 * server. Data are send using ResultsData structure, which provides string
	 * representations of test data and serialization for simple socket
	 * communication.
	 * 
	 * @param testCaseElement
	 * @param phase
	 */
	public void sendData(ITestCaseElement testCaseElement, Phase phase) {
		log.info("Sending Result data to ClientHandlers");
		ResultsData data = new ResultsData(new StringTestCaseElement(testCaseElement), phase);

		// add to list
		resultsList.add(data);

		Iterator<ClientHandler> allClients = clientThreads.iterator();
		while (allClients.hasNext()) {
			ClientHandler client = allClients.next();
			client.sendData(data);
		}
	}

	/**
	 * Sends ITestRunSession data to all clients who are connected to this
	 * server. Data are send using ResultsData structure, which provides string
	 * representations of test data and serialization for simple socket
	 * communication.
	 * 
	 * @param testRunSession
	 * @param phase
	 */
	public void sendData(ITestRunSession testRunSession, Phase phase) {
		log.info("Sending Failure data to ClientHandlers");
		ResultsData data = new ResultsData(new StringTestRunSession(testRunSession), phase);

		// add to list
		resultsList.add(data);

		Iterator<ClientHandler> allClients = clientThreads.iterator();
		while (allClients.hasNext()) {
			ClientHandler client = allClients.next();
			client.sendData(data);
		}
	}

	/**
	 * TODO: Will be this even necessary?
	 */
	public void endConnections() {
		Iterator<ClientHandler> allClients = clientThreads.iterator();
		while (allClients.hasNext()) {
			ClientHandler client = allClients.next();
			// client.sendData("end");
			client = null;
		}
	}

}
