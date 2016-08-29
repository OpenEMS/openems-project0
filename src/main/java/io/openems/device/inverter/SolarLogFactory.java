package io.openems.device.inverter;

import java.util.HashMap;

import com.google.gson.JsonObject;

import io.openems.channel.ChannelWorker;
import io.openems.config.ConfigUtil;
import io.openems.config.exception.ConfigException;
import io.openems.device.Device;
import io.openems.device.DeviceFactory;

public class SolarLogFactory extends DeviceFactory {

	@Override
	public Device getDevice(String name, JsonObject device, HashMap<String, ChannelWorker> channels)
			throws ConfigException {
		String channel = ConfigUtil.getAsString(device, "channel");
		int unitid = ConfigUtil.getAsInt(device, "modbusUnit");
		int totalPower = ConfigUtil.getAsInt(device, "totalPower");
		SolarLog sl = new SolarLog(name, channel, unitid, totalPower);
		return sl;
	}

	@Override
	public JsonObject getConfig(Device d) {
		if (d instanceof SolarLog) {
			JsonObject jo = new JsonObject();
			SolarLog s = (SolarLog) d;
			jo.addProperty("type", s.getClass().getName());
			jo.addProperty("channel", s.getChannel());
			jo.addProperty("modbusUnit", s.getUnitid());
			jo.addProperty("totalPower", s.getTotalPower());
			return jo;
		}
		return null;
	}

}
