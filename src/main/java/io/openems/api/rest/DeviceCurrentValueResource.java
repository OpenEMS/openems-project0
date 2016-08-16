package io.openems.api.rest;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.xml.sax.SAXException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import io.openems.App;
import io.openems.channel.modbus.WritableModbusDevice;
import io.openems.device.Device;
import io.openems.device.protocol.BitElement;
import io.openems.device.protocol.BitsElement;
import io.openems.device.protocol.ModbusElement;

public class DeviceCurrentValueResource extends ServerResource {

	@Get("json")
	public Representation getCurrentValue() throws IOException, ParserConfigurationException, SAXException {
		String device = (String) this.getRequestAttributes().get("device");
		String parameterName = (String) this.getRequestAttributes().get("parametername");
		Device d = App.getConfig().getDevices().get(device);
		return new StringRepresentation(findElement(parameterName, d).getAsJson().toString(),
				MediaType.APPLICATION_JSON);
	}

	@Post("json")
	public void setValue(String json) throws IOException, ParserConfigurationException, SAXException {
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(json);
		String device = (String) this.getRequestAttributes().get("device");
		String parameterName = (String) this.getRequestAttributes().get("parametername");
		WritableModbusDevice d = (WritableModbusDevice) App.getConfig().getDevices().get(device);
		ModbusElement<?> e = findElement(parameterName, d);
		if (e instanceof BitElement) {
			d.addToWriteQueue(e, jsonElement.getAsJsonObject().get("value").getAsBoolean());
		} else {
			d.addToWriteQueue(e, e.toRegister(jsonElement.getAsJsonObject().get("value")));
		}
	}

	public ModbusElement<?> findElement(String name, Device d) {
		if (d.getElements().contains(name)) {
			return d.getElement(name);
		}
		for (String s : d.getElements()) {
			ModbusElement<?> e = d.getElement(s);
			if (e instanceof BitsElement) {
				BitsElement be = (BitsElement) e;
				if (be.getBitElements().containsKey(name)) {
					return be.getBit(name);
				}
			}
		}
		return null;
	}
}
