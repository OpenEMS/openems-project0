package de.fenecon.openems.device.counter;

import com.google.gson.JsonObject;

import de.fenecon.openems.device.Device;
import de.fenecon.openems.device.DeviceFactory;

public class SocomecFactory extends DeviceFactory {

	@Override
	public Device getDevice(String name, JsonObject device) throws Exception {
		Socomec socomec = new Socomec(name, device.get("channel").getAsString(), device.get("modbusUnit").getAsInt());
		socomec.init();
		return socomec;
	}

}
