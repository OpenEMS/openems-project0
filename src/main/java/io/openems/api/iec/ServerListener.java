package io.openems.api.iec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ServerEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class ServerListener implements ServerEventListener {

	private final static Logger log = LoggerFactory.getLogger(ServerListener.class);

	private int connectionIdCounter = 1;

	private final JsonObject config;

	public ServerListener() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		super();
		File file = new File("/etc/openemsIEC");
		// File file = new
		// File("C:/Users/matthias.rossmann/Dev/git/openems/openemsIEC");
		log.info("Read configuration from " + file.getAbsolutePath());
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(new FileReader(file));
		config = jsonElement.getAsJsonObject();
	}

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
			connection.waitForStartDT(new ConnectionListener(connection, myConnectionId, config), 5000);
		} catch (IOException e) {
			log.debug("Connection (" + myConnectionId + ") interrupted while waiting for StartDT: " + e.getMessage()
					+ ". Will quit.");
			return;
		} catch (TimeoutException e) {
		}

		log.debug("Started data transfer on connection (" + myConnectionId + ") Will listen for incoming commands.");
	}

	@Override
	public void serverStoppedListeningIndication(IOException arg0) {
		log.debug("Connection attempt failed: " + arg0.getMessage());
	}

}
