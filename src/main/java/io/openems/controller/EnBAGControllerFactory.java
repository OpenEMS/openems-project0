package io.openems.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.openems.channel.ChannelWorker;
import io.openems.config.ConfigUtil;
import io.openems.config.exception.ConfigException;
import io.openems.device.Device;
import io.openems.device.counter.Counter;
import io.openems.device.ess.Ess;
import io.openems.device.inverter.SolarLog;
import io.openems.device.io.IO;

public class EnBAGControllerFactory extends ControllerFactory {

	private final static Logger log = LoggerFactory.getLogger(EnBAGControllerFactory.class);

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
		Counter pvCounter = (Counter) devices.get(ConfigUtil.getAsString(controllerJson, "pvCounter"));
		boolean chargeFromAc = ConfigUtil.getAsBoolean(controllerJson, "chargeFromAc");

		Gson gson = new Gson();
		Map<String, String> essOffGridSwitches = new HashMap<String, String>();
		String jsonOffGridSwitches = ConfigUtil.getAsJsonElement(controllerJson, "essOffGridSwitches").toString();
		essOffGridSwitches = gson.fromJson(jsonOffGridSwitches, essOffGridSwitches.getClass());

		int maxGridFeedPower = ConfigUtil.getAsInt(controllerJson, "maxGridFeedPower");
		String pvOnGridSwitch = ConfigUtil.getAsString(controllerJson, "pvOnGridSwitch");
		String pvOffGridSwitch = ConfigUtil.getAsString(controllerJson, "pvOffGridSwitch");
		String primaryEss = ConfigUtil.getAsString(controllerJson, "primaryEss");
		IO io = (IO) devices.get(ConfigUtil.getAsString(controllerJson, "io"));
		SolarLog solarLog = (SolarLog) devices.get(ConfigUtil.getAsString(controllerJson, "solarLog"));
		int cycle = ConfigUtil.getAsInt(controllerJson, "cycle");
		return new ControllerWorker(name,
				channelWorkers.values(), new EnBAGController(name, counter, ess, chargeFromAc, maxGridFeedPower,
						pvOnGridSwitch, pvOffGridSwitch, essOffGridSwitches, primaryEss, io, solarLog, pvCounter),
				cycle);
	}

	@Override
	public JsonObject getConfig(ControllerWorker worker) {
		try {
			if (worker.getController() instanceof EnBAGController) {
				JsonObject jo = new JsonObject();
				EnBAGController bal = (EnBAGController) worker.getController();
				jo.addProperty("type", bal.getClass().getName());
				jo.addProperty("chargeFromAc", bal.isAllowChargeFromAC());
				jo.addProperty("gridCounter", bal.getGridCounter().getName());
				JsonArray arr = new JsonArray();
				jo.add("ess", arr);
				for (Map.Entry<String, Ess> ess : bal.getEssDevices().entrySet()) {
					arr.add(ess.getKey());
				}
				JsonObject offGridSwitches = new JsonObject();
				jo.add("essOffGridSwitches", offGridSwitches);
				for (Map.Entry<String, String> value : bal.getEssOffGridSwitches().entrySet()) {
					offGridSwitches.addProperty(value.getKey(), value.getValue());
				}
				jo.addProperty("maxGridFeedPower", bal.getMaxGridFeedPower());
				jo.addProperty("pvOnGridSwitch", bal.getPvOnGridSwitch());
				jo.addProperty("pvOffGridSwitch", bal.getPvOffGridSwitch());
				jo.addProperty("primaryEss", bal.getPrimaryOffGridEss());
				jo.addProperty("io", bal.getIo().getName());
				jo.addProperty("solarLog", bal.getSolarLog().getName());
				jo.addProperty("cycle", worker.getCycle());
				jo.addProperty("pvCounter", bal.getPvCounter().getName());
				return jo;
			}
		} catch (Exception e) {
			log.error("an error occured on generationg the Json config!");
			return null;
		}
		return null;
	}

}
