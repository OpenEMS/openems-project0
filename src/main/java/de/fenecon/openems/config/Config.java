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

import de.fenecon.openems.controller.ControllerWorker;
import de.fenecon.openems.modbus.ModbusWorker;
import de.fenecon.openems.modbus.device.counter.Counter;
import de.fenecon.openems.modbus.device.ess.Ess;
import de.fenecon.openems.monitoring.MonitoringWorker;

public class Config {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(Config.class.getName());

	private final String devicekey;
	private final HashMap<String, ModbusWorker> modbuss;
	private final HashMap<String, Ess> esss;
	private final HashMap<String, Counter> counters;
	private final HashMap<String, ControllerWorker> controllers;
	private final HashMap<String, MonitoringWorker> monitorings;

	public Config(String devicekey, HashMap<String, ModbusWorker> modbuss, HashMap<String, Ess> esss,
			HashMap<String, Counter> counters, HashMap<String, ControllerWorker> controllers,
			HashMap<String, MonitoringWorker> monitorings) {
		this.devicekey = devicekey;
		this.modbuss = modbuss;
		this.esss = esss;
		this.counters = counters;
		this.controllers = controllers;
		this.monitorings = monitorings;
	}

	public String getDevicekey() {
		return devicekey;
	}

	public HashMap<String, ModbusWorker> getModbuss() {
		return modbuss;
	}

	public HashMap<String, Ess> getEsss() {
		return esss;
	}

	public HashMap<String, Counter> getCounters() {
		return counters;
	}

	public HashMap<String, ControllerWorker> getControllers() {
		return controllers;
	}

	public HashMap<String, MonitoringWorker> getMonitorings() {
		return monitorings;
	}

	@Override
	public String toString() {
		return "Config [modbusWorkers=" + modbuss + ", esss=" + esss + ", counters=" + counters + ", controllerWorkers="
				+ controllers + "]";
	}
}