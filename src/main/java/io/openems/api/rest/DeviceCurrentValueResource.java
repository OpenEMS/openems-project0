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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.openems.App;
import io.openems.channel.modbus.WritableModbusDevice;
import io.openems.channel.modbus.write.ModbusCoilWriteRequest;
import io.openems.channel.modbus.write.ModbusSingleRegisterWriteRequest;
import io.openems.config.ConfigUtil;
import io.openems.device.Device;
import io.openems.device.protocol.BitElement;
import io.openems.device.protocol.BitsElement;
import io.openems.device.protocol.ModbusElement;
import io.openems.device.protocol.interfaces.DoublewordElement;
import io.openems.device.protocol.interfaces.WordElement;
import io.openems.element.InvalidValueExcecption;

public class DeviceCurrentValueResource extends ServerResource {

	@Get("json")
	public Representation getCurrentValue() throws IOException, ParserConfigurationException, SAXException {
		String device = (String) this.getRequestAttributes().get("device");
		String parameterName = (String) this.getRequestAttributes().get("parametername");
		Device d = App.getConfig().getDevices().get(device);
		try {
			return new StringRepresentation(findElement(parameterName, d).getAsJson().toString(),
					MediaType.APPLICATION_JSON);
		} catch (InvalidValueExcecption e) {
			JsonObject obj = new JsonObject();
			obj.addProperty("error", e.getMessage());
			return new StringRepresentation(obj.toString(), MediaType.APPLICATION_JSON);
		}
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
			BitElement be = (BitElement) e;
			d.addToWriteRequestQueue(new ModbusCoilWriteRequest(be, ConfigUtil.getAsBoolean(jsonElement, "value")));
		} else if (e instanceof WordElement<?>) {
			WordElement<?> we = (WordElement<?>) e;
			d.addToWriteRequestQueue(new ModbusSingleRegisterWriteRequest(we,
					we.toRegister(ConfigUtil.getAsJsonElement(jsonElement, "value"))));
		} else if (e instanceof DoublewordElement) {
			// DoublewordElement dwe = (DoublewordElement) e;
			throw new UnsupportedOperationException("not implemented");
		} else {
			throw new UnsupportedOperationException("not implemented");
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
