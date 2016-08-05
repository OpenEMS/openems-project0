package de.fenecon.openems.monitoring;

import java.util.HashMap;

import com.google.gson.JsonObject;

import de.fenecon.openems.device.Device;

public abstract class MonitorFactory {

	public abstract MonitoringWorker getMonitoringWorker(String name, JsonObject monitor,
			HashMap<String, Device> devices) throws Exception;
}
