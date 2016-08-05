package io.openems.controller;

import io.openems.channel.ChannelWorker;
import io.openems.device.Device;

import java.util.Map;

import com.google.gson.JsonObject;

public abstract class ControllerFactory {

	public abstract ControllerWorker getControllerWorker(String name, JsonObject controller,
			Map<String, Device> devices, Map<String, ChannelWorker> channelWorkers) throws Exception;
}
