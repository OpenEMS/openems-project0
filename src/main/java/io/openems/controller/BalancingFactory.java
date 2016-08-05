package io.openems.controller;

import io.openems.channel.ChannelWorker;
import io.openems.device.Device;
import io.openems.device.counter.Counter;
import io.openems.device.ess.Ess;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BalancingFactory extends ControllerFactory {

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
		Counter counter = (Counter) devices.get(controllerJson.get("gridCounter").getAsString());
		boolean chargeFromAc = controllerJson.get("chargeFromAc").getAsBoolean();
		return new ControllerWorker(name, channelWorkers.values(), new Balancing(name, counter, ess, chargeFromAc));
	}
}
