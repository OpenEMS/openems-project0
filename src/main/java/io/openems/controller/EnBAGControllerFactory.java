package io.openems.controller;

import io.openems.channel.ChannelWorker;
import io.openems.device.Device;
import io.openems.device.counter.Counter;
import io.openems.device.ess.Ess;
import io.openems.device.inverter.SolarLog;
import io.openems.device.io.IO;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class EnBAGControllerFactory extends ControllerFactory {

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

		Gson gson = new Gson();
		Map<String, String> essOffGridSwitches = new HashMap<String, String>();
		essOffGridSwitches = gson.fromJson(controllerJson.get("essOffGridSwitches").toString(),
				essOffGridSwitches.getClass());

		return new ControllerWorker(name, channelWorkers.values(), new EnBAGController(name, counter, ess,
				chargeFromAc, controllerJson.get("maxGridFeedPower").getAsInt(), controllerJson.get("pvOnGridSwitch")
						.getAsString(), controllerJson.get("pvOffGridSwitch").getAsString(), essOffGridSwitches,
				controllerJson.get("primaryEss").getAsString(),
				(IO) devices.get(controllerJson.get("io").getAsString()), (SolarLog) devices.get(controllerJson.get(
						"solarlog").getAsString())));
	}

	@Override
	public JsonObject getConfig(ControllerWorker worker) {
		// TODO Auto-generated method stub
		return null;
	}

}
