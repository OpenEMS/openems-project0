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

	@Override
	public JsonObject getConfig(Device d) {
		if (d instanceof Wago) {
			JsonObject jo = new JsonObject();
			Wago w = (Wago) d;
			jo.addProperty("type", w.getClass().getName());
			jo.addProperty("channel", w.getChannel());
			jo.addProperty("modbusUnit", w.getUnitid());
			jo.addProperty("inetAddress", w.getIp().getHostAddress());
			return jo;
		}
		return null;
	}

}
