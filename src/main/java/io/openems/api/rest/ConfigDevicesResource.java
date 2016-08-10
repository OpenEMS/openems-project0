package io.openems.api.rest;

import io.openems.App;
import io.openems.config.Config;

import javax.xml.parsers.ParserConfigurationException;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.xml.sax.SAXException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ConfigDevicesResource extends ServerResource {

	@Get("json")
	public String getConfig() {
		return App.getConfig().getConfigAsJson().toString();
	}

	@Post("json")
	public void setConfig(String json) throws ParserConfigurationException, SAXException, Exception {
		System.out.println(json);
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(json);
		System.out.println(jsonElement.toString());
		App.updateConfig(new Config(jsonElement.getAsJsonObject()));
	}
}
