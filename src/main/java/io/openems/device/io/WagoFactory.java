package io.openems.device.io;

import io.openems.device.Device;
import io.openems.device.DeviceFactory;

import java.net.InetAddress;

import com.google.gson.JsonObject;

public class WagoFactory extends DeviceFactory {

	@Override
	public Device getDevice(String name, JsonObject device) throws Exception {
		Wago wago = new Wago(name, device.get("channel").getAsString(), device.get("modbusUnit").getAsInt(),
				InetAddress.getByName(device.get("inetAddress").getAsString()));
		wago.init();
		return wago;
	}

}
