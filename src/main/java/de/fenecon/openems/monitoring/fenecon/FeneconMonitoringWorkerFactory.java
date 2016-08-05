package de.fenecon.openems.monitoring.fenecon;

import java.util.HashMap;

import com.google.gson.JsonObject;

import de.fenecon.openems.device.Device;
import de.fenecon.openems.monitoring.MonitorFactory;
import de.fenecon.openems.monitoring.MonitoringWorker;

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
