package io.openems.device.ess;

import io.openems.device.Device;
import io.openems.device.DeviceFactory;

import com.google.gson.JsonObject;

public class CommercialFactory extends DeviceFactory {

	@Override
	public Device getDevice(String name, JsonObject device) throws Exception {
		Commercial c = new Commercial(name, device.get("channel").getAsString(), device.get("modbusUnit").getAsInt());
		return c;
	}

	@Override
	public JsonObject getConfig(Device d) {
		if (d instanceof Commercial) {
			JsonObject jo = new JsonObject();
			Commercial c = (Commercial) d;
			jo.addProperty("type", c.getClass().getName());
			jo.addProperty("channel", c.getChannel());
			jo.addProperty("modbusUnit", c.getUnitid());
			return jo;
		}
		return null;
	}
}
