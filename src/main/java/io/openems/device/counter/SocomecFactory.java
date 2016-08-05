package io.openems.device.counter;

import io.openems.device.Device;
import io.openems.device.DeviceFactory;

import com.google.gson.JsonObject;

public class SocomecFactory extends DeviceFactory {

	@Override
	public Device getDevice(String name, JsonObject device) throws Exception {
		Socomec socomec = new Socomec(name, device.get("channel").getAsString(), device.get("modbusUnit").getAsInt());
		socomec.init();
		return socomec;
	}

}
