package io.openems.api.iec;

import io.openems.App;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ServerEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerListener implements ServerEventListener {

	private final static Logger log = LoggerFactory.getLogger(ServerListener.class);

	private int connectionIdCounter = 1;

	@Override
	public void connectionAttemptFailed(IOException arg0) {
		log.debug("Server has stopped listening for new connections : \"" + arg0.getMessage() + "\". Will quit.");
	}

	@Override
	public void connectionIndication(Connection connection) {
		int myConnectionId = connectionIdCounter++;
		log.debug("A client has connected using TCP/IP. Will listen for a StartDT request. Connection ID: "
				+ myConnectionId);

		try {
			connection.waitForStartDT(new ConnectionListener(connection, myConnectionId), 5000);
		} catch (IOException e) {
			log.debug("Connection (" + myConnectionId + ") interrupted while waiting for StartDT: " + e.getMessage()
					+ ". Will quit.");
			return;
		} catch (TimeoutException e) {
		}

		log.debug("Started data transfer on connection (" + myConnectionId + ") Will listen for incoming commands.");
		registerElementChangedListener(connection);
	}

	@Override
	public void serverStoppedListeningIndication(IOException arg0) {
		log.debug("Connection attempt failed: " + arg0.getMessage());
	}

	public void registerElementChangedListener(Connection connection) {
		for (IecControllable d : App.getConfig().getDevices().values()) {
			// d.createChangeListeners(startAddress, connection);
		}
	}
}
