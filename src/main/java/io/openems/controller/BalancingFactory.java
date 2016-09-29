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
import io.openems.device.counter.Counter;
import io.openems.device.ess.Ess;

public class BalancingFactory extends ControllerFactory {

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
		String channel = ConfigUtil.getAsString(controllerJson, "gridCounter");
		Counter counter = (Counter) devices.get(channel);
		boolean chargeFromAc = ConfigUtil.getAsBoolean(controllerJson, "chargeFromAc");
		int cycle = ConfigUtil.getAsInt(controllerJson, "cycle");
		return new ControllerWorker(name, channelWorkers.values(), new Balancing(name, counter, ess, chargeFromAc),
				cycle);
	}

	@Override
	public JsonObject getConfig(ControllerWorker worker) {
		if (worker.getController() instanceof Balancing) {
			JsonObject jo = new JsonObject();
			Balancing bal = (Balancing) worker.getController();
			jo.addProperty("type", bal.getClass().getName());
			jo.addProperty("chargeFromAc", bal.isAllowChargeFromAC());
			jo.addProperty("gridCounter", bal.getGridCounter().getName());
			JsonArray arr = new JsonArray();
			jo.add("ess", arr);
			for (Map.Entry<String, Ess> ess : bal.getEssDevices().entrySet()) {
				arr.add(ess.getKey());
			}
			return jo;
		}
		return null;
	}
}
