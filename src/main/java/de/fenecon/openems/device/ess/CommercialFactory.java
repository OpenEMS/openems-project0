package de.fenecon.openems.device.ess;

import com.google.gson.JsonObject;

import de.fenecon.openems.device.Device;
import de.fenecon.openems.device.DeviceFactory;

public class CommercialFactory extends DeviceFactory {

	@Override
	public Device getDevice(String name, JsonObject device) throws Exception {
		Commercial c = new Commercial(name, device.get("channel").getAsString(), device.get("modbusUnit").getAsInt());
		c.init();
		return c;
	}
}
