package io.openems.device.ess;

import java.util.HashMap;

import com.google.gson.JsonObject;

import io.openems.channel.ChannelWorker;
import io.openems.config.ConfigUtil;
import io.openems.config.exception.ConfigException;
import io.openems.device.Device;
import io.openems.device.DeviceFactory;

public class MiniFactory extends DeviceFactory {

	@Override
	public JsonObject getConfig(Device d) {
		if (d instanceof Mini) {
			JsonObject jo = new JsonObject();
			Mini m = (Mini) d;
			jo.addProperty("type", m.getClass().getName());
			jo.addProperty("channel", m.getChannel());
			jo.addProperty("modbusUnit", m.getUnitid());
			return jo;
		}
		return null;
	}

	@Override
	public Device getDevice(String name, JsonObject device, HashMap<String, ChannelWorker> channels)
			throws ConfigException {
		String channel = ConfigUtil.getAsString(device, "channel");
		int unitid = ConfigUtil.getAsInt(device, "modbusUnit");
		int minSoc = ConfigUtil.getAsInt(device, "minSoc");
		Mini m = new Mini(name, channel, unitid, minSoc);
		return m;
	}
}
