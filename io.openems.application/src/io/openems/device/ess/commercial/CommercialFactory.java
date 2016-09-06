package io.openems.device.ess.commercial;

import java.util.HashMap;

import com.google.gson.JsonObject;

import io.openems.channel.ChannelWorker;
import io.openems.config.ConfigUtil;
import io.openems.config.exception.ConfigException;
import io.openems.device.Device;
import io.openems.device.DeviceFactory;

public class CommercialFactory extends DeviceFactory {

	@Override
	public Device getDevice(String name, JsonObject device, HashMap<String, ChannelWorker> channels)
			throws ConfigException {
		String channel = ConfigUtil.getAsString(device, "channel");
		int unitid = ConfigUtil.getAsInt(device, "modbusUnit");
		int minSoc = ConfigUtil.getAsInt(device, "minSoc");
		Commercial c = new Commercial(name, channel, unitid, minSoc);
		return c;
	}

	@Override
	public JsonObject getConfig(Device d) {
		if (d instanceof Commercial) {
			JsonObject jo = new JsonObject();
			Commercial c = (Commercial) d;
			jo.addProperty("type", c.getClass().getName());
			jo.addProperty("channel", c.getChannel());
			jo.addProperty("modbusUnit", c.getUnitid());
			jo.addProperty("minSoc", c.getMinSoc());
			return jo;
		}
		return null;
	}
}
