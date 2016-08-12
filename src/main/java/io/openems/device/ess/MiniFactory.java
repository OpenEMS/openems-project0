package io.openems.device.ess;

import java.util.HashMap;

import com.google.gson.JsonObject;

import io.openems.channel.ChannelWorker;
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
	public Device getDevice(String name, JsonObject device, HashMap<String, ChannelWorker> channels) throws Exception {
		Mini m = new Mini(name, device.get("channel").getAsString(), device.get("modbusUnit").getAsInt(),
				device.get("minSoc").getAsInt());
		return m;
	}
}
