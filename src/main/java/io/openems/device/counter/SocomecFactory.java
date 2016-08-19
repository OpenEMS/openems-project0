package io.openems.device.counter;

import io.openems.channel.ChannelWorker;
import io.openems.device.Device;
import io.openems.device.DeviceFactory;

import java.util.HashMap;

import com.google.gson.JsonObject;

public class SocomecFactory extends DeviceFactory {

	@Override
	public Device getDevice(String name, JsonObject device, HashMap<String, ChannelWorker> channels) throws Exception {
		Socomec socomec = new Socomec(name, device.get("channel").getAsString(), device.get("modbusUnit").getAsInt(),
				device.get("inverted").getAsBoolean());
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
			jo.addProperty("inverted", s.isInverted());
			return jo;
		}
		return null;
	}

}
