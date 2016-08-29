package io.openems.monitoring.fenecon;

import java.util.HashMap;

import com.google.gson.JsonObject;

import io.openems.config.ConfigUtil;
import io.openems.config.exception.ConfigException;
import io.openems.device.Device;
import io.openems.monitoring.MonitorFactory;
import io.openems.monitoring.MonitoringWorker;

public class FeneconMonitoringWorkerFactory extends MonitorFactory {

	@Override
	public MonitoringWorker getMonitoringWorker(String name, JsonObject monitorJson, HashMap<String, Device> devices)
			throws ConfigException {
		String devicekey = ConfigUtil.getAsString(monitorJson, "devicekey");
		FeneconMonitoringWorker feneconMonitoring = new FeneconMonitoringWorker(devicekey);
		for (Device device : devices.values()) { // add listener for all
													// elements
			device.addOnChangeListener(feneconMonitoring);
		}
		return feneconMonitoring;
	}

	@Override
	public JsonObject getConfig(MonitoringWorker worker) {
		if (worker instanceof FeneconMonitoringWorker) {
			JsonObject jo = new JsonObject();
			FeneconMonitoringWorker fmw = (FeneconMonitoringWorker) worker;
			jo.addProperty("type", fmw.getClass().getName());
			jo.addProperty("devicekey", fmw.getDevicekey());
			return jo;
		}
		return null;
	}
}
