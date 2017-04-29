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
	// socket for communicating with client
	private Socket clientSocket;
	// output stream for sending ResultsData to client
	private ObjectOutputStream outputStream;
	// list of ResultsData used to send initial data set after client connects
	private List<ResultsData> resultsList;

	/**
	 * Constructor.
	 * 
	 * @param socket
	 */
	public ClientHandler(Socket socket, List<ResultsData> resultsList) {
		log.info("ClientHandler@" + this.getId() + " created");
		clientSocket = socket;
		this.resultsList = resultsList;

		// establish output stream
		try {
			outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
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

		// TODO: send initial data set
		try {
			outputStream.writeObject(resultsList);
		} catch (IOException e) {
			log.error("ClientHandler@" + this.getId() + ": Failed to send data to client: "
					+ clientSocket.getInetAddress().getHostName());
			e.printStackTrace();
		}

		// TODO: communication, interrupt will be used when client ends to end thread
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}

		// close resources
		resultsList.clear();
		try {
			outputStream.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send ResultsData to client handled by ClientHandler's thread. 
	 * 
	 * @param desc
	 * @param phase
	 */
	void sendData(ResultsData data) {
		log.info("ClientHandler@" + this.getId() + ": Sending data to client");
		try {
			outputStream.writeObject(data);
		} catch (IOException e) {
			log.error("ClientHandler@" + this.getId() + ": Failed to send data to client: "
					+ clientSocket.getInetAddress().getHostName());
			e.printStackTrace();
		}
	}
}
