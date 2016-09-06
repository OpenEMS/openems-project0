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
import io.openems.device.io.IO;

public class IOControllerFactory extends ControllerFactory {

	@Override
	public ControllerWorker getControllerWorker(String name, JsonObject controllerJson, Map<String, Device> devices,
			Map<String, ChannelWorker> channelWorkers) throws ConfigException {
		HashMap<String, IO> io = new HashMap<>();
		JsonArray ioJsonArray = ConfigUtil.getAsJsonArray(controllerJson, "io");
		for (JsonElement ioJsonElement : ioJsonArray) {
			String ioDevice = ioJsonElement.getAsString();
			Device device = devices.get(ioDevice);
			if (device instanceof IO) {
				io.put(ioDevice, (IO) device);
			}
		}

		return new ControllerWorker(name, channelWorkers.values(), new IOController(name, io));
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
