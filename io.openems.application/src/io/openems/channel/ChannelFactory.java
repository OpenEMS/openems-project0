package io.openems.channel;

import com.google.gson.JsonObject;

import io.openems.config.exception.ConfigException;

public abstract class ChannelFactory {

	public abstract ChannelWorker getChannelWorker(String name, JsonObject channel) throws ConfigException;

	public abstract JsonObject getConfig(ChannelWorker worker);
}
