package io.openems.config.api;

import io.openems.App;

import java.util.Map.Entry;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.JsonObject;

public class ConfigDevicesResource extends ServerResource {

	@Get("json")
	public String listDevices() {
		JsonObject devices = new JsonObject();
		for (Entry<String, JsonObject> entry : App.getConfig().getJsonDevices().entrySet()) {
			devices.add(entry.getKey(), entry.getValue());
		}
		JsonObject controllers = new JsonObject();
		for (Entry<String, JsonObject> entry : App.getConfig().getJsonControllers().entrySet()) {
			controllers.add(entry.getKey(), entry.getValue());
		}
		JsonObject channels = new JsonObject();
		for (Entry<String, JsonObject> entry : App.getConfig().getJsonCannels().entrySet()) {
			channels.add(entry.getKey(), entry.getValue());
		}
		JsonObject monitors = new JsonObject();
		for (Entry<String, JsonObject> entry : App.getConfig().getJsonMonitors().entrySet()) {
			monitors.add(entry.getKey(), entry.getValue());
		}
		JsonObject obj = new JsonObject();
		obj.add("devices", devices);
		obj.add("controller", controllers);
		obj.add("channel", channels);
		obj.add("monitor", monitors);
		return obj.toString();
	}
}
