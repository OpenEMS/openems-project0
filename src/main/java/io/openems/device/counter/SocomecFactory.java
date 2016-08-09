package io.openems.device.counter;

import io.openems.device.Device;
import io.openems.device.DeviceFactory;

import com.google.gson.JsonObject;

public class SocomecFactory extends DeviceFactory {

	@Override
	public Device getDevice(String name, JsonObject device) throws Exception {
		Socomec socomec = new Socomec(name, device.get("channel").getAsString(), device.get("modbusUnit").getAsInt());
		return socomec;
	}

	@Override
	public JsonObject getConfig(Device d) {
		if (d instanceof Socomec) {
			JsonObject jo = new JsonObject();
			Socomec s = (Socomec) d;
			jo.addProperty("type", s.getClass().getName());
			jo.addProperty("channel", s.getChannel());
			jo.addProperty("modbusUnit", s.getUnitid());
			return jo;
		}
		return null;
	}

}
