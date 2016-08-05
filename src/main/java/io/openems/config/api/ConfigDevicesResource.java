package io.openems.config.api;

import io.openems.App;

import java.util.Map.Entry;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ConfigDevicesResource extends ServerResource {

	@Get("json")
	public String listDevices() {
		JsonArray arr = new JsonArray();
		for (Entry<String, JsonObject> entry : App.getConfig().getJsonDevices().entrySet()) {
			arr.add(entry.getKey());
		}
		JsonObject obj = new JsonObject();
		obj.add("devices", arr);
		return obj.toString();
	}
}
