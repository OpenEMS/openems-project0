package io.openems.api.rest;

import io.openems.config.api.ConfigDevicesResource;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

public class RestWorker extends Application {
	private static Component worker = null;

	private static final int PORT = 8084;

	public static Component startWorker() throws Exception {
		if (worker == null) {
			worker = new Component();
			worker.getServers().add(Protocol.HTTP, PORT);
			worker.getDefaultHost().attach("/rest", new RestWorker());
			worker.start();
		}
		return worker;
	}

	public static void stopWorker() throws Exception {
		if (worker != null) {
			worker.stop();
			worker = null;
		}
	}

	/**
	 * Creates a root Restlet that will receive all incoming calls.
	 */
	@Override
	public synchronized Restlet createInboundRoot() {
		Router router = new Router(getContext());
		// define all routes
		router.attach("/config", ConfigDevicesResource.class);

		return router;
	}
}
