package mcoufal.inrunjunit.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestRunSession;

import mcoufal.inrunjunit.listener.JUnitListenerEP.Phase;

/**
 * Represents server gathering test data from JUnitListener and sending this
 * data to all clients that had connected to this server. Manages list of
 * ClientHandlers to provide service for all clients.
 * 
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 */
public class ResultsServer extends Thread {
	// set up one port number: 7357 (TEST)
	public static int portNum = 7357;
	// list of client connections
	private List<ClientHandler> clientThreads = new ArrayList<ClientHandler>();
	// list of all ResultsData from JUnitListenerEP
	private List<ResultsData> resultsList = new ArrayList<ResultsData>();
	// server socket
	private ServerSocket servSock = null;
	// used to control server thread life cycle
	private Boolean alive = true;

	/**
	 * Waiting for incoming client communication, creating new ClientHandlers
	 * for every client and managing those handlers.
	 */
	@Override
	public void run() {
		// establish server socket
		try {
			servSock = new ServerSocket(portNum);
		} catch (IOException e) {
			System.err.println("Failed to create server socket!");
			e.printStackTrace();
		}

		// looking for clients
		while (alive) {
			try {
				ClientHandler newClient = new ClientHandler(this, servSock.accept(), resultsList);
				clientThreads.add(newClient);
				newClient.start();
			} catch (IOException e) {
				endConnections();
				resultsList.clear();
				clientThreads.clear();
				alive = false;
			}
		}

		// close resources
		try {
			servSock.close();
		} catch (IOException e) {
			e.printStackTrace();
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
		ResultsData data = new ResultsData(new StringTestCaseElement(testCaseElement), phase);

		// add to list
		resultsList.add(data);

		// send to all clients
		for (ClientHandler client : clientThreads) {
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
		ResultsData data = null;
		if (phase.equals(Phase.SESSION_FINISHED))
			data = new ResultsData(new StringTestRunSession(testRunSession, true), phase);
		else
			data = new ResultsData(new StringTestRunSession(testRunSession, false), phase);
		// add to list
		resultsList.add(data);

		// send to all clients
		for (ClientHandler client : clientThreads) {
			client.sendData(data);
		}
	}

	/**
	 * @return server socket
	 */
	public ServerSocket getServSock() {
		return servSock;
	}

	/* TODO: will need synchronization! */
	public void handlerExit(ClientHandler handler) {
		clientThreads.remove(handler);
	}

	/**
	 * Ends all client handler threads.
	 */
	public void endConnections() {
		for (ClientHandler client : clientThreads) {
			client.endHandler();
		}
	}

}
