package io.openems.api.iec;

import java.io.IOException;

import org.openmuc.j60870.Server;

public class Iec60870Server {

	public void start() {
		Server server = new Server.Builder().build();

		try {
			server.start(new ServerListener());
		} catch (IOException e) {
			System.out.println("Unable to start listening: \"" + e.getMessage() + "\". Will quit.");
			return;
		}
	}
}
