package io.openems.controller;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.openems.channel.ChannelWorker;
import io.openems.device.Device;
import io.openems.device.ess.Ess;
import io.openems.device.io.IO;

public class MiniTestFactory extends ControllerFactory {

	@Override
	public ControllerWorker getControllerWorker(String name, JsonObject controllerJson, Map<String, Device> devices,
			Map<String, ChannelWorker> channelWorkers) throws Exception {
		Map<String, Ess> ess = new HashMap<>();
		JsonArray essJsonArray = controllerJson.get("ess").getAsJsonArray();
		for (JsonElement essJsonElement : essJsonArray) {
			String essDevice = essJsonElement.getAsString();
			Device device = devices.get(essDevice);
			if (device instanceof Ess) {
				ess.put(essDevice, (Ess) device);
			}
		}
		return new ControllerWorker(name, channelWorkers.values(), new MiniTest(name, ess));
	}

	@Override
	public JsonObject getConfig(ControllerWorker worker) {
		if (worker.getController() instanceof IOController) {
			JsonObject jo = new JsonObject();
			IOController ioc = (IOController) worker.getController();
			jo.addProperty("type", ioc.getClass().getName());
			JsonArray arr = new JsonArray();
			jo.add("io", arr);
			for (Map.Entry<String, IO> io : ioc.getIo().entrySet()) {
				arr.add(io.getKey());
			}
			return jo;
		}
		return null;
	}

}
