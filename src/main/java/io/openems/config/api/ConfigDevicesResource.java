package io.openems.config.api;

import io.openems.App;
import io.openems.config.Config;

import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.xml.sax.SAXException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
		obj.add("device", devices);
		obj.add("controller", controllers);
		obj.add("channel", channels);
		obj.add("monitor", monitors);
		return obj.toString();
	}

	@Post("json")
	public void listDevices(String json) throws ParserConfigurationException, SAXException, Exception {
		System.out.println(json);
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(json);
		System.out.println(jsonElement.toString());
		App.updateConfig(new Config(jsonElement.getAsJsonObject()));
	}
}
