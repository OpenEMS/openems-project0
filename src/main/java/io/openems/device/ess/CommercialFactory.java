package io.openems.device.ess;

import io.openems.device.Device;
import io.openems.device.DeviceFactory;

import com.google.gson.JsonObject;

public class CommercialFactory extends DeviceFactory {

	@Override
	public Device getDevice(String name, JsonObject device) throws Exception {
		Commercial c = new Commercial(name, device.get("channel").getAsString(), device.get("modbusUnit").getAsInt());
		c.init();
		return c;
	}
}
