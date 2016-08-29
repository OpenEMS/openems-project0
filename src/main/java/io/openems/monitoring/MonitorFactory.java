package io.openems.monitoring;

import java.util.HashMap;

import com.google.gson.JsonObject;

import io.openems.config.exception.ConfigException;
import io.openems.device.Device;

public abstract class MonitorFactory {

	public abstract MonitoringWorker getMonitoringWorker(String name, JsonObject monitor,
			HashMap<String, Device> devices) throws ConfigException;

	public abstract JsonObject getConfig(MonitoringWorker worker);
}
