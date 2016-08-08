package io.openems.monitoring.fenecon;

import io.openems.device.Device;
import io.openems.monitoring.MonitorFactory;
import io.openems.monitoring.MonitoringWorker;

import java.util.HashMap;

import com.google.gson.JsonObject;

public class FeneconMonitoringWorkerFactory extends MonitorFactory {

	@Override
	public MonitoringWorker getMonitoringWorker(String name, JsonObject monitorJson, HashMap<String, Device> devices)
			throws Exception {
		FeneconMonitoringWorker feneconMonitoring = new FeneconMonitoringWorker(monitorJson.get("devicekey")
				.getAsString());
		for (Device device : devices.values()) { // add listener for all
													// elements
			for (String elementName : device.getElements()) {
				device.getElement(elementName).addListener(feneconMonitoring);
			}
		}
		return feneconMonitoring;
	}
}