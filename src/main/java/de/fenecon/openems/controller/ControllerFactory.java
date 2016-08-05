package de.fenecon.openems.controller;

import java.util.Map;

import com.google.gson.JsonObject;

import de.fenecon.openems.channel.ChannelWorker;
import de.fenecon.openems.device.Device;

public abstract class ControllerFactory {

	public abstract ControllerWorker getControllerWorker(String name, JsonObject controller,
			Map<String, Device> devices, Map<String, ChannelWorker> channelWorkers) throws Exception;
}
