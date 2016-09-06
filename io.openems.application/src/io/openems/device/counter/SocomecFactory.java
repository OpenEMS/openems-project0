package io.openems.device.counter;

import java.util.HashMap;

import com.google.gson.JsonObject;

import io.openems.channel.ChannelWorker;
import io.openems.config.ConfigUtil;
import io.openems.config.exception.ConfigException;
import io.openems.device.Device;
import io.openems.device.DeviceFactory;

public class SocomecFactory extends DeviceFactory {

	@Override
	public Device getDevice(String name, JsonObject device, HashMap<String, ChannelWorker> channels)
			throws ConfigException {
		String channel = ConfigUtil.getAsString(device, "channel");
		int unitid = ConfigUtil.getAsInt(device, "modbusUnit");
		Socomec socomec = new Socomec(name, channel, unitid);
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
			return jo;
		}
		return null;
	}

}
