package io.openems.controller;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.openems.channel.ChannelWorker;
import io.openems.config.ConfigUtil;
import io.openems.config.exception.ConfigException;
import io.openems.device.Device;
import io.openems.device.ess.Ess;

public class MiniTestFactory extends ControllerFactory {

	@Override
	public ControllerWorker getControllerWorker(String name, JsonObject controllerJson, Map<String, Device> devices,
			Map<String, ChannelWorker> channelWorkers) throws ConfigException {
		Map<String, Ess> ess = new HashMap<>();
		JsonArray essJsonArray = ConfigUtil.getAsJsonArray(controllerJson, "ess");
		for (JsonElement essJsonElement : essJsonArray) {
			String essDevice = essJsonElement.getAsString();
			Device device = devices.get(essDevice);
			if (device instanceof Ess) {
				ess.put(essDevice, (Ess) device);
			}
		}
		int cycle = ConfigUtil.getAsInt(controllerJson, "cycle");
		return new ControllerWorker(name, channelWorkers.values(), new MiniTest(name, ess), cycle);
	}

	@Override
	public JsonObject getConfig(ControllerWorker worker) {
		if (worker.getController() instanceof IOController) {
			JsonObject jo = new JsonObject();
			IOController ioc = (IOController) worker.getController();
			jo.addProperty("type", ioc.getClass().getName());
			JsonArray arr = new JsonArray();
			jo.add("io", arr);
			// for (Map.Entry<String, IO> io : ioc.getIo().entrySet()) {
			// arr.add(io.getKey());
			// }
			return jo;
		}
		return null;
	}

}
