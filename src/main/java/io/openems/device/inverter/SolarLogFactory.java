package io.openems.device.inverter;

import io.openems.channel.ChannelWorker;
import io.openems.device.Device;
import io.openems.device.DeviceFactory;

import java.util.HashMap;

import com.google.gson.JsonObject;

public class SolarLogFactory extends DeviceFactory {

	@Override
	public Device getDevice(String name, JsonObject device, HashMap<String, ChannelWorker> channels) throws Exception {
		SolarLog sl = new SolarLog(name, device.get("channel").getAsString(), device.get("modbusUnit").getAsInt(),
				device.get("totalPower").getAsInt());
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
