package io.openems.device;

import io.openems.channel.ChannelWorker;

import java.util.HashMap;

import com.google.gson.JsonObject;

public abstract class DeviceFactory {

	public abstract Device getDevice(String name, JsonObject device, HashMap<String, ChannelWorker> channels)
			throws Exception;

	public abstract JsonObject getConfig(Device d);
}
