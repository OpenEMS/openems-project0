package io.openems.api.rest;

import io.openems.App;
import io.openems.device.Device;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.xml.sax.SAXException;

public class DeviceCurrentValueResource extends ServerResource {

	@Get
	public Representation getCurrentValue() throws IOException, ParserConfigurationException, SAXException {
		String device = (String) this.getRequestAttributes().get("device");
		String parameterName = (String) this.getRequestAttributes().get("parametername");
		Device d = App.getConfig().getDevices().get(device);
		return new StringRepresentation(d.getElement(parameterName).getAsJson().toString(), MediaType.APPLICATION_JSON);
	}
}
