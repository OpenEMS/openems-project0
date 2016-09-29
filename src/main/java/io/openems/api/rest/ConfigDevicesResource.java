package io.openems.api.rest;

import java.io.IOException;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import io.openems.App;
import io.openems.config.Config;
import io.openems.config.exception.ConfigException;

public class ConfigDevicesResource extends ServerResource {

	@Get("json")
	public String getConfig() {
		return App.getConfig().getConfigAsJson().toString();
	}

	@Post("json")
	public void setConfig(String json) throws ConfigException {
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(json);
		Config conf = new Config(jsonElement.getAsJsonObject());
		try {
			conf.writeJsonFile();
		} catch (IOException e) {
			new ConfigException("Can't write Config file!");
		}
		App.updateConfig(conf);
	}
}
