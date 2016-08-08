package io.openems.controller;

import io.openems.channel.ChannelWorker;
import io.openems.device.Device;
import io.openems.device.io.IO;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class IOControllerFactory extends ControllerFactory {

	@Override
	public ControllerWorker getControllerWorker(String name, JsonObject controllerJson, Map<String, Device> devices,
			Map<String, ChannelWorker> channelWorkers) throws Exception {
		HashMap<String, IO> io = new HashMap<>();
		JsonArray ioJsonArray = controllerJson.get("io").getAsJsonArray();
		for (JsonElement ioJsonElement : ioJsonArray) {
			String ioDevice = ioJsonElement.getAsString();
			Device device = devices.get(ioDevice);
			if (device instanceof IO) {
				io.put(ioDevice, (IO) device);
			}
		}

		return new ControllerWorker(name, channelWorkers.values(), new IOController(name, io));
	}

}