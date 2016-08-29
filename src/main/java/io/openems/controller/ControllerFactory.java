package io.openems.controller;

import java.util.Map;

import com.google.gson.JsonObject;

import io.openems.channel.ChannelWorker;
import io.openems.config.exception.ConfigException;
import io.openems.device.Device;

public abstract class ControllerFactory {

	public abstract ControllerWorker getControllerWorker(String name, JsonObject controller,
			Map<String, Device> devices, Map<String, ChannelWorker> channelWorkers) throws ConfigException;

	public abstract JsonObject getConfig(ControllerWorker worker);
}
