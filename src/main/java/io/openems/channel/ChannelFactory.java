package io.openems.channel;

import com.google.gson.JsonObject;

public abstract class ChannelFactory {

	public abstract ChannelWorker getChannelWorker(String name, JsonObject channel) throws Exception;

	public abstract JsonObject getConfig(ChannelWorker worker);
}
