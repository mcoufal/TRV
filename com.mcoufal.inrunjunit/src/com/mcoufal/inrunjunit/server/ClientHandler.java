package com.mcoufal.inrunjunit.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import org.jboss.reddeer.common.logging.Logger;

/**
 * This class represents thread handling one client connection. In case of
 * multiple clients, client server multiple ClientHandlers while communication
 * with all of them.
 * 
 * @author Martin Coufal, xcoufa08@stud.fit.vutbr.cz
 */
public class ClientHandler extends Thread {
	// set up logger
	private final static Logger log = Logger.getLogger(ClientHandler.class);
	// TODO: reference to this client handler -- usage for synchronization
	private ClientHandler handler = null;
	// TODO reference to results server -- usage for synchronization
	private ResultsServer server = null;
	// socket for communicating with client
	private Socket clientSocket;
	// output stream for sending ResultsData to client
	private ObjectOutputStream toClient;
	// list of ResultsData used to send initial data set after client connects
	private List<ResultsData> resultsList;
	// serves as ending indicator (if set to false, thread will be ended)
	private Boolean alive;

	/**
	 * Constructor.
	 * 
	 * @param socket
	 */
	public ClientHandler(ResultsServer server, Socket socket, List<ResultsData> resultsList) {
		log.info("ClientHandler@" + this.getId() + " created");
		this.handler = this;
		this.server = server;
		clientSocket = socket;
		this.resultsList = resultsList;

		// establish output stream
		try {
			toClient = new ObjectOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			log.error("ClientHandler@" + this.getId() + ": Failed to establish output stream to client: "
					+ clientSocket.getInetAddress().getHostName());
			e.printStackTrace();
		}

		log.info("ClientHandler@" + this.getId() + " initialized");
	}

	/**
	 * TODO:
	 */
	@Override
	public void run() {
		log.info("ClientHandler@" + this.getId() + " started");
		log.info("ClientHandler@" + this.getId() + ": Sending initial data set to client");
		alive = true;

		// TODO: send initial data set
		try {
			toClient.writeObject(resultsList);
		} catch (IOException e) {
			log.error("ClientHandler@" + this.getId() + ": Failed to send data to client: "
					+ clientSocket.getInetAddress().getHostName());
			e.printStackTrace();
		}

		// TODO: communication
		while (alive) {
			// space for communication with client
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// close resources
		try {
			toClient.close();
		} catch (IOException e) {
			// empty
		}
		try {
			clientSocket.close();
		} catch (IOException e) {
			// empty
		}
	}

	/**
	 * Send ResultsData to client handled by ClientHandler's thread. 
	 * 
	 * @param desc
	 * @param phase
	 */
	public void sendData(ResultsData data) {
		log.info("ClientHandler@" + this.getId() + ": Sending data to client");
		try {
			toClient.writeObject(data);
		} catch (IOException e) {
			// try once more
			try {
				toClient.writeObject(data);
			} catch (IOException e1) {
				// client probably ended, remove from list of clients
				log.info("ClientHandler@" + this.getId() + ": Failed to send data to client: "
						+ clientSocket.getInetAddress().getHostName());
				//log.info("Removing client from list of clients...");
				// FIXME: needs to use synchronizations, else throws ConcurrentModificationException
				//server.handlerExit(handler);
				endHandler();
			}
		}
	}

	/**
	 * Initializes end of this client handler.
	 */
	public void endHandler() {
		alive = false;
	}
}
