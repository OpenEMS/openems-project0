package io.openems.device.inverter;

import io.openems.device.Device;
import io.openems.device.DeviceFactory;

import com.google.gson.JsonObject;

public class SolarLogFactory extends DeviceFactory {

	@Override
	public Device getDevice(String name, JsonObject device) throws Exception {
		SolarLog sl = new SolarLog(name, device.get("channel").getAsString(), device.get("modbusUnit").getAsInt(),
				device.get("modbusUnit").getAsInt());
		sl.init();
		return sl;
	}

}
