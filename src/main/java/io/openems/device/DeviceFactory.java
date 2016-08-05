package io.openems.device;

import com.google.gson.JsonObject;

public abstract class DeviceFactory {

	public abstract Device getDevice(String name, JsonObject device) throws Exception;
}
