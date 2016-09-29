package io.openems.api.rest;

import java.util.Arrays;
import java.util.HashSet;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.service.CorsService;

public class RestWorker extends Application {
	private static Component worker = null;

	private static final int PORT = 8084;

	public RestWorker() {
		CorsService corsService = new CorsService();
		corsService.setAllowedOrigins(
				new HashSet<String>(Arrays.asList("http://localhost:3000", "https://192.168.178.177:4200")));
		// TODO add URL of real web interface
		corsService.setAllowedCredentials(true);
		getServices().add(corsService);
		getStatusService().setConverterService(new RestConverterService());
	}

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
		router.attach("/device/{device}/current/{parametername}", DeviceCurrentValueResource.class);

		return router;
	}
}
