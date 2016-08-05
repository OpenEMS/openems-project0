package io.openems.monitoring;

import io.openems.device.Device;

import java.util.HashMap;

import com.google.gson.JsonObject;

public abstract class MonitorFactory {

	public abstract MonitoringWorker getMonitoringWorker(String name, JsonObject monitor,
			HashMap<String, Device> devices) throws Exception;
}
