/*
 * OpenEMS - Open Source Energy Management System
 * Copyright (c) 2016 FENECON GmbH & Co. KG
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.fenecon.openems.config;

import java.util.HashMap;
import java.util.logging.Logger;

import de.fenecon.openems.channel.ChannelWorker;
import de.fenecon.openems.controller.ControllerWorker;
import de.fenecon.openems.device.Device;
import de.fenecon.openems.monitoring.MonitoringWorker;

public class Config {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(Config.class.getName());

	private final String devicekey;
	private final HashMap<String, ChannelWorker> channelWorkers;
	private final HashMap<String, Device> devices;
	private final HashMap<String, ControllerWorker> controllerWorkers;
	private final HashMap<String, MonitoringWorker> monitoringWorkers;

	public Config(String devicekey, HashMap<String, ChannelWorker> channelWorkers, HashMap<String, Device> devices,
			HashMap<String, ControllerWorker> controllerWorkers, HashMap<String, MonitoringWorker> monitoringWorkers) {
		this.devicekey = devicekey;
		this.channelWorkers = channelWorkers;
		this.devices = devices;
		this.controllerWorkers = controllerWorkers;
		this.monitoringWorkers = monitoringWorkers;
	}

	public String getDevicekey() {
		return devicekey;
	}

	public HashMap<String, ChannelWorker> getChannelWorkers() {
		return channelWorkers;
	}

	public HashMap<String, Device> getDevices() {
		return devices;
	}

	public HashMap<String, ControllerWorker> getControllerWorkers() {
		return controllerWorkers;
	}

	public HashMap<String, MonitoringWorker> getMonitoringWorkers() {
		return monitoringWorkers;
	}

	@Override
	public String toString() {
		return "Config [devicekey=" + devicekey + ", channelWorkers=" + channelWorkers + ", devices=" + devices
				+ ", controllerWorkers=" + controllerWorkers + ", monitoringWorkers=" + monitoringWorkers + "]";
	}
}