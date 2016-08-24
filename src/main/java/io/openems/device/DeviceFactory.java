package io.openems.device;

import java.util.HashMap;

import com.google.gson.JsonObject;

import io.openems.channel.ChannelWorker;
import io.openems.config.exception.ConfigException;

public abstract class DeviceFactory {

	public abstract Device getDevice(String name, JsonObject device, HashMap<String, ChannelWorker> channels)
			throws ConfigException;

	public abstract JsonObject getConfig(Device d);
}
