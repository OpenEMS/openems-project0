package de.fenecon.openems.device.io;

import java.net.InetAddress;

import com.google.gson.JsonObject;

import de.fenecon.openems.device.Device;
import de.fenecon.openems.device.DeviceFactory;

public class WagoFactory extends DeviceFactory {

	@Override
	public Device getDevice(String name, JsonObject device) throws Exception {
		Wago wago = new Wago(name, device.get("channel").getAsString(), device.get("modbusUnit").getAsInt(),
				InetAddress.getByName(device.get("inetAddress").getAsString()));
		wago.init();
		return wago;
	}

}
